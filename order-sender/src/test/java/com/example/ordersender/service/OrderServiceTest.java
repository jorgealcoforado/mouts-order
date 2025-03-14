package com.example.ordersender.service;

import com.example.common.dto.OrderDTO;
import com.example.common.entity.Order;
import com.example.common.enums.OrderStatus;
import com.example.common.mapper.OrderMapper;
import com.example.ordersender.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
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
        order.setStatus(OrderStatus.PROCESSED);
        order.setSentAt(null);

        orderDTO = OrderMapper.INSTANCE.orderToOrderDTO(order);
    }

    @Test
    void testSendOrderToB_Success() {
        when(orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId())).thenReturn(Optional.of(order));

        orderService.sendOrderToB(orderDTO);

        assertEquals(OrderStatus.SENT_TO_B, order.getStatus());
        assertNotNull(order.getSentAt());
        verify(orderRepository, times(1)).save(order);
        verify(kafkaTemplate, times(1)).send(eq("order-sent-topic"), any(OrderDTO.class));
    }

    @Test
    void testSendOrderToB_OrderNotFound() {
        when(orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.sendOrderToB(orderDTO));
        assertEquals("Pedido não encontrado", exception.getMessage());
    }
}