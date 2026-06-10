package org.deloitte.onlineorderapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.deloitte.onlineorderapp.dto.OrderRequest;
import org.deloitte.onlineorderapp.dto.OrderResponse;
import org.deloitte.onlineorderapp.entity.Order;
import org.deloitte.onlineorderapp.entity.OrderStatus;
import org.deloitte.onlineorderapp.repository.OrderRepository;
import org.deloitte.onlineorderapp.service.OrderService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = modelMapper.map(orderRequest, Order.class);

        BigDecimal unitPrice = BigDecimal.valueOf(100);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(orderRequest.quantity()));

        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        return modelMapper.map(order, OrderResponse.class);
    }
}