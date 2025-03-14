package com.example.orderreceiver.event;

import com.example.common.dto.OrderDTO;
import com.example.orderreceiver.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderReceiverConsumer {
    private final OrderService orderService;

    public OrderReceiverConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-received-topic", groupId = "order-group")
    public void receiveOrder(OrderDTO orderDTO) {
        orderService.processIncomingOrder(orderDTO);
    }
}
