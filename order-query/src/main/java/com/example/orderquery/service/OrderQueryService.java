package com.example.orderquery.service;

import com.example.common.dto.OrderDTO;
import com.example.common.entity.Order;
import com.example.orderquery.repository.OrderRepository;
import com.example.common.mapper.OrderMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderQueryService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public OrderDTO getOrderById(String externalOrderId) {
        Optional<Order> order = orderRepository.findByExternalOrderId(externalOrderId);
        return order.map(orderMapper::orderToOrderDTO)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    public Page<OrderDTO> getOrdersByDate(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate, pageable);
        return orders.map(orderMapper::orderToOrderDTO);
    }
}
