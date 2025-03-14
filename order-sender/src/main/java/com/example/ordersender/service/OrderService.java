package com.example.ordersender.service;

import com.example.common.dto.OrderDTO;
import com.example.common.entity.Order;
import com.example.common.enums.OrderStatus;
import com.example.common.mapper.OrderMapper;
import com.example.ordersender.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, OrderDTO> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderToB(OrderDTO orderDTO) {
        Order order = orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.setStatus(OrderStatus.SENT_TO_B);
        order.setSentAt(LocalDateTime.now());
        orderRepository.save(order);

        OrderDTO updatedOrderDTO = OrderMapper.INSTANCE.orderToOrderDTO(order);
        kafkaTemplate.send("order-sent-topic", updatedOrderDTO);
    }
}
