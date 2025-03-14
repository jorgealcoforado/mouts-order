package com.example.orderprocessor.service;

import com.example.common.dto.OrderDTO;
import com.example.common.entity.Order;
import com.example.common.enums.OrderStatus;
import com.example.common.mapper.OrderMapper;
import com.example.orderprocessor.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, OrderDTO> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processOrder(OrderDTO orderDTO) {
        Order order = orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        BigDecimal total = order.getProducts().stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(total);
        order.setStatus(OrderStatus.PROCESSED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        OrderDTO updatedOrderDTO = OrderMapper.INSTANCE.orderToOrderDTO(order);
        kafkaTemplate.send("order-send-topic", updatedOrderDTO);
    }
}
