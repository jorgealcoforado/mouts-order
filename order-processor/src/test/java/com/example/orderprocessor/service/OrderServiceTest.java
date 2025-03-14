package com.example.orderprocessor.service;

import com.example.common.dto.OrderDTO;
import com.example.common.dto.ProductDTO;
import com.example.common.entity.Order;
import com.example.common.entity.Product;
import com.example.common.enums.OrderStatus;
import com.example.orderprocessor.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Product product1 = new Product(null, null, "PROD001", new BigDecimal("19.99"), 2);
        Product product2 = new Product(null, null, "PROD002", new BigDecimal("49.90"), 1);
        Product product3 = new Product(null, null, "PROD003", new BigDecimal("99.50"), 3);
        List<Product> products = Arrays.asList(product1, product2, product3);

        order = new Order();
        order.setExternalOrderId("ORD123401");
        order.setStatus(OrderStatus.RECEIVED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setProducts(products);
        order.setTotalPrice(BigDecimal.ZERO);

        List<ProductDTO> productDTOs = products.stream()
                .map(p -> new ProductDTO(p.getSku(), p.getPrice(), p.getQuantity()))
                .toList();

        orderDTO = new OrderDTO("ORD123401", productDTOs);
    }

    @Test
    void testProcessOrderSuccessfully() {
        when(orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId())).thenReturn(Optional.of(order));

        orderService.processOrder(orderDTO);

        assertEquals(OrderStatus.PROCESSED, order.getStatus());
        assertEquals(new BigDecimal("388.38"), order.getTotalPrice());
        verify(orderRepository, times(1)).save(order);
        verify(kafkaTemplate, times(1)).send(eq("order-send-topic"), any(OrderDTO.class));
    }

    @Test
    void testProcessOrderThrowsExceptionWhenNotFound() {
        when(orderRepository.findByExternalOrderId(orderDTO.getExternalOrderId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.processOrder(orderDTO));
        assertEquals("Pedido não encontrado", exception.getMessage());
    }
}
