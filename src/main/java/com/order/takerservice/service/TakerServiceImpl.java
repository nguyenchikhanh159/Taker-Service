package com.order.takerservice.service;

import com.order.takerservice.exception.InsufficientTicketException;
import com.order.takerservice.exception.InvalidOrderMessage;
import com.order.takerservice.model.*;
import com.order.takerservice.service.kafka.KafKaSender;
import com.order.takerservice.service.interfaces.TakerService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.order.takerservice.constant.UrlConstant.ticketUrl;

@Service
public class TakerServiceImpl implements TakerService {

    private final KafKaSender kafKaSender;

    private final RestTemplate restTemplate;

    public TakerServiceImpl(KafKaSender kafKaSender,
                            RestTemplate restTemplate) {
        this.kafKaSender = kafKaSender;
        this.restTemplate = restTemplate;
    }

    @Override
    public Message sendOrderMessage(OrderMessage orderMessage, String topic) {
        validateOrder(orderMessage);
        return kafKaSender.sendMessage(orderMessage, topic);
    }

    private void validateOrder(OrderMessage orderMessage) {
        if (isOrderMessage(orderMessage)) {
            throw new InvalidOrderMessage("Invalid order message");
        }

        List<OrderDetailDto> orderDetailDto = orderMessage.getOrderDetailDtoList();
        List<TicketDto> ticketDtoList = getRemainingTickets();
        Map<Integer, Integer> ticketMap = buildTicketHashMap(ticketDtoList);

        if (!checkRemainingTickets(ticketMap, orderDetailDto)) {
            throw new InsufficientTicketException("Not enough available tickets.");
        }
    }

    private boolean isOrderMessage(OrderMessage orderMessage) {
        return orderMessage != null && orderMessage.getOrderDto() != null &&
                !CollectionUtils.isEmpty(orderMessage.getOrderDetailDtoList());
    }

    private List<TicketDto> getRemainingTickets() {
        TicketDto[] ticketDto = restTemplate.getForObject(ticketUrl, TicketDto[].class);
        return List.of(Objects.requireNonNull(ticketDto));
    }

    private Map<Integer, Integer> buildTicketHashMap(List<TicketDto> ticketDtoList) {
        Map<Integer, Integer> ticketMap = new HashMap<>();
        for (TicketDto ticketDto : ticketDtoList) {
            ticketMap.put(ticketDto.getTicketId(), ticketDto.getAvailability());
        }
        return ticketMap;
    }

    private boolean checkRemainingTickets(Map<Integer, Integer> ticketMap, List<OrderDetailDto> orderDetailDtoList) {
        for (OrderDetailDto orderDetailDto : orderDetailDtoList) {
            return isInsufficientTicket(ticketMap, orderDetailDto);
        }
        return false;
    }

    private boolean isInsufficientTicket(Map<Integer, Integer> ticketMap, OrderDetailDto orderDetailDto) {
        return ticketMap.containsKey(orderDetailDto.getTicketId())
                && (orderDetailDto.getQuantity()
                - ticketMap.get(orderDetailDto.getTicketId()) < 0);
    }
}
