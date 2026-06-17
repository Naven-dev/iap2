package org.deloitte.onlineorderapp.service.impl;

import org.deloitte.onlineorderapp.config.ProductClient;
import org.deloitte.onlineorderapp.dto.OrderRequest;
import org.deloitte.onlineorderapp.dto.OrderResponse;
import org.deloitte.onlineorderapp.dto.ProductResponse;
import org.deloitte.onlineorderapp.entity.Order;
import org.deloitte.onlineorderapp.exception.OrderServiceException;
import org.deloitte.onlineorderapp.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_success() {
        OrderRequest req = new OrderRequest();
        req.setProductId(1L);
        req.setQuantity(2);

        ProductResponse product = new ProductResponse();
        product.setId(1L);
        product.setPrice(10.0);

        Order mappedOrder = new Order();
        mappedOrder.setProductId(1L);
        mappedOrder.setQuantity(2);

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setProductId(1L);
        savedOrder.setQuantity(2);
        savedOrder.setTotalPrice(BigDecimal.valueOf(20.0));

        OrderResponse response = new OrderResponse();
        response.setId(100L);
        response.setProductId(1L);
        response.setQuantity(2);
        response.setTotalPrice(BigDecimal.valueOf(20.0));

        when(productClient.getProduct(1L)).thenReturn(product);
        when(modelMapper.map(req, Order.class)).thenReturn(mappedOrder);
        when(orderRepository.save(mappedOrder)).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderResponse.class)).thenReturn(response);

        OrderResponse actual = orderService.createOrder(req);

        assertNotNull(actual);
        assertEquals(100L, actual.getId());
        assertEquals(BigDecimal.valueOf(20.0), actual.getTotalPrice());

        verify(productClient).getProduct(1L);
        verify(orderRepository).save(mappedOrder);
    }

    @Test
    void createOrder_productNotFound_throwsOrderServiceException() {
        OrderRequest req = new OrderRequest();
        req.setProductId(42L);
        req.setQuantity(1);

        when(productClient.getProduct(42L)).thenReturn(null);

        OrderServiceException ex = assertThrows(OrderServiceException.class, () -> orderService.createOrder(req));
        assertTrue(ex.getMessage().contains("Product not found with id: 42"));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_productClientThrows_wrappedAsOrderServiceException() {
        OrderRequest req = new OrderRequest();
        req.setProductId(99L);
        req.setQuantity(1);

        RuntimeException cause = new RuntimeException("feign error");
        when(productClient.getProduct(99L)).thenThrow(cause);

        OrderServiceException ex = assertThrows(OrderServiceException.class, () -> orderService.createOrder(req));
        assertEquals("Failed to create order", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void getAllOrders_success() {
        Order o = new Order();
        o.setId(1L);
        o.setProductId(2L);
        o.setQuantity(3);

        OrderResponse resp = new OrderResponse();
        resp.setId(1L);
        resp.setProductId(2L);
        resp.setQuantity(3);

        when(orderRepository.findAll()).thenReturn(List.of(o));
        when(modelMapper.map(o, OrderResponse.class)).thenReturn(resp);

        List<OrderResponse> all = orderService.getAllOrders();
        assertNotNull(all);
        assertEquals(1, all.size());
        assertEquals(1L, all.get(0).getId());
    }

    @Test
    void getAllOrders_repositoryThrowsOrderServiceException_isWrapped() {
        when(orderRepository.findAll()).thenThrow(new OrderServiceException("db error"));

        OrderServiceException ex = assertThrows(OrderServiceException.class, () -> orderService.getAllOrders());
        assertEquals("Failed to retrieve all orders", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void getOrderById_success() {
        Long id = 5L;
        Order o = new Order();
        o.setId(id);
        o.setProductId(7L);
        o.setQuantity(2);

        OrderResponse resp = new OrderResponse();
        resp.setId(id);
        resp.setProductId(7L);
        resp.setQuantity(2);

        when(orderRepository.findById(id)).thenReturn(Optional.of(o));
        when(modelMapper.map(o, OrderResponse.class)).thenReturn(resp);

        OrderResponse result = orderService.getOrderById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getOrderById_notFound_throwsOrderServiceException() {
        Long id = 999L;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        OrderServiceException ex = assertThrows(OrderServiceException.class, () -> orderService.getOrderById(id));
        assertTrue(ex.getMessage().contains("Order not found with id: " + id));
    }

    @Test
    void getOrderById_repositoryThrows_wrappedInOrderServiceException() {
        Long id = 7L;
        RuntimeException cause = new RuntimeException("db down");
        when(orderRepository.findById(id)).thenThrow(cause);

        OrderServiceException ex = assertThrows(OrderServiceException.class, () -> orderService.getOrderById(id));
        assertTrue(ex.getMessage().contains("Failed to retrieve order with id: " + id));
        assertSame(cause, ex.getCause());
    }
}

