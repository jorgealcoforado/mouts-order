package com.example.ordersender.event;

import com.example.common.dto.OrderDTO;
import com.example.ordersender.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderSenderConsumer {
    private final OrderService orderService;

    public OrderSenderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-send-topic", groupId = "order-group")
    public void sendOrder(OrderDTO orderDTO) {
        orderService.sendOrderToB(orderDTO);
    }
}
