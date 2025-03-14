package com.example.orderreceiver.service;

import com.example.common.dto.OrderDTO;
import com.example.common.dto.ProductDTO;
import com.example.common.entity.Order;
import com.example.common.enums.OrderStatus;
import com.example.orderreceiver.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, OrderDTO> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setExternalOrderId("ORD123401");
        order.setStatus(OrderStatus.RECEIVED);
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());

        orderDTO = new OrderDTO("ORD123401", List.of(
                new ProductDTO("PROD001", null, 2),
                new ProductDTO("PROD002", null, 1)
        ));
    }

    @Test
    void testProcessIncomingOrder_NewOrder() {
        when(orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.processIncomingOrder(orderDTO);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaTemplate, times(1)).send(eq("order-processing-topic"), eq(orderDTO));
    }

    @Test
    void testProcessIncomingOrder_DuplicateOrder() {
        when(orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId())).thenReturn(Optional.of(order));

        orderService.processIncomingOrder(orderDTO);

        verify(orderRepository, never()).save(any(Order.class));
        verify(kafkaTemplate, times(1)).send(eq("order-duplicate-topic"), eq(orderDTO));
    }
}
