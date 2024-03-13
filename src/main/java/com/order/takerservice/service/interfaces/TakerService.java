package com.order.takerservice.service.interfaces;

import com.order.takerservice.model.Message;
import com.order.takerservice.model.OrderMessage;

public interface TakerService {
     Message sendOrderMessage(OrderMessage orderMessage, String topic);
}
