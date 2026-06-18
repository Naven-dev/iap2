package org.deloitte.onlineorderapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.deloitte.onlineorderapp.config.ProductClient;
import org.deloitte.onlineorderapp.dto.OrderRequest;
import org.deloitte.onlineorderapp.dto.OrderResponse;
import org.deloitte.onlineorderapp.dto.ProductResponse;
import org.deloitte.onlineorderapp.entity.Order;
import org.deloitte.onlineorderapp.entity.OrderStatus;
import org.deloitte.onlineorderapp.exception.OrderServiceException;
import org.deloitte.onlineorderapp.repository.OrderRepository;
import org.deloitte.onlineorderapp.service.OrderService;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        try {
            ProductResponse product = productClient.getProduct(orderRequest.getProductId());

            if (product == null ) {
                throw new OrderServiceException("Product not found with id: " + orderRequest.getProductId());
            }

            Order order = modelMapper.map(orderRequest, Order.class);
            order.setId(null);
            order.setTotalPrice(BigDecimal.valueOf(product.getPrice() * orderRequest.getQuantity()));
            order.setStatus(OrderStatus.CREATED);

            Order saved = orderRepository.save(order);
            return modelMapper.map(saved, OrderResponse.class);
        } catch (FeignException.NotFound e) {

            throw new OrderServiceException("Product not found with id: " + orderRequest.getProductId());
        }  catch (OrderServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderServiceException("Failed to create order", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        try {
            return orderRepository.findAll()
                    .stream()
                    .map(order -> modelMapper.map(order, OrderResponse.class))
                    .toList();
        } catch (Exception e) {
            throw new OrderServiceException("Failed to retrieve all orders", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new OrderServiceException("Order not found with id: " + id));

            return modelMapper.map(order, OrderResponse.class);
        } catch (OrderServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderServiceException("Failed to retrieve order with id: " + id, e);
        }
    }
}