package com.example.orderreceiver.service;

import com.example.common.dto.OrderDTO;
import com.example.common.entity.Order;
import com.example.common.enums.OrderStatus;
import com.example.common.mapper.OrderMapper;
import com.example.orderreceiver.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, OrderDTO> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processIncomingOrder(OrderDTO orderDTO) {
        Optional<Order> existingOrder = orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId());

        if (existingOrder.isPresent()) {
            kafkaTemplate.send("order-duplicate-topic", orderDTO);
            return;
        }

        Order order = OrderMapper.INSTANCE.orderDTOToOrder(orderDTO);
        order.setStatus(OrderStatus.RECEIVED);
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);
        kafkaTemplate.send("order-processing-topic", orderDTO);
    }
}
