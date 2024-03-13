package com.order.takerservice.controller;

import com.order.takerservice.model.Message;
import com.order.takerservice.model.OrderMessage;
import com.order.takerservice.service.interfaces.TakerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.order.takerservice.constant.KafKaConstant.TICKET_ORDERS_TOPIC;

@RestController
@RequestMapping("/api")
public class TicketTakerController {

    private final TakerService takerService;

    public TicketTakerController(TakerService takerService) {
        this.takerService = takerService;
    }

    @PostMapping(path = "/ticket-orders/v1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> createOrders(@RequestBody OrderMessage orderMessage) {
        Message message = takerService.sendOrderMessage(orderMessage, TICKET_ORDERS_TOPIC);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
}
