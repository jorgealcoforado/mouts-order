package com.example.orderprocessor.event;

import com.example.common.dto.OrderDTO;
import com.example.orderprocessor.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessorConsumer {
    private final OrderService orderService;

    public OrderProcessorConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-processing-topic", groupId = "order-group")
    public void processOrder(OrderDTO orderDTO) {
        orderService.processOrder(orderDTO);
    }
}
