package org.deloitte.onlineorderapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.deloitte.onlineorderapp.config.ProductClient;
import org.deloitte.onlineorderapp.dto.OrderRequest;
import org.deloitte.onlineorderapp.dto.OrderResponse;
import org.deloitte.onlineorderapp.dto.ProductResponse;
import org.deloitte.onlineorderapp.entity.Order;
import org.deloitte.onlineorderapp.repository.OrderRepository;
import org.deloitte.onlineorderapp.service.OrderService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {

        ProductResponse product = productClient.getProduct(orderRequest.getProductId());

        Order order = new Order();
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setTotalPrice(BigDecimal.valueOf(product.getPrice() * orderRequest.getQuantity()));

        Order saved = orderRepository.save(order);
        return modelMapper.map(saved, OrderResponse.class);


       // Order order = modelMapper.map(orderRequest, Order.class);

//        ProductResponse product = productClient.getProduct(productId);
//        Order order = modelMapper.map(product,Order.class);
//
//        Order savedOrder =  orderRepository.save(order);
//        return modelMapper.map(savedOrder,ProductResponse.class);


//        BigDecimal unitPrice = BigDecimal.valueOf(100);
//        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(orderRequest.quantity()));
//
//        order.setTotalPrice(totalPrice);
//        order.setStatus(OrderStatus.CREATED);
//        order.setCreatedAt(LocalDateTime.now());
//
//        Order savedOrder = orderRepository.save(order);
//        return modelMapper.map(savedOrder, OrderResponse.class);
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