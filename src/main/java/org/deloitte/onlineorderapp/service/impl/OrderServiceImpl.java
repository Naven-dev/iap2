package org.deloitte.onlineorderapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackCreateOrder")
    @Retry(name = "productService")
    public OrderResponse createOrder(OrderRequest orderRequest) {
        try {
            ProductResponse product = productClient.getProduct(orderRequest.getProductId());

            if (product == null ) {
                throw new OrderServiceException("Product not found with id: " + orderRequest.getProductId());
            }
            log.info("Creating Order: {}", orderRequest);
            Order order = modelMapper.map(orderRequest, Order.class);
            order.setId(null);
            //order.setTotalPrice(BigDecimal.valueOf(product.getPrice() * orderRequest.getQuantity()));
            order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(orderRequest.getQuantity())));
            order.setStatus(OrderStatus.CREATED);

            Order saved = orderRepository.save(order);
            return modelMapper.map(saved, OrderResponse.class);
        } catch (FeignException.NotFound e) {
            log.error("Failed to connect FeignClient {}", e.getMessage());
            throw new OrderServiceException("Product not found with id: " + orderRequest.getProductId());
        }  catch (OrderServiceException e) {
            log.error("Failed to create order: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage());
            throw new OrderServiceException("Failed to create order", e);
        }
    }

    public OrderResponse fallbackCreateOrder(OrderRequest orderRequest, Throwable throwable) {
        log.error("Circuit Breaker / Fallback triggered for product ID {}. Reason: {}",
                orderRequest.getProductId(), throwable.getMessage());

        // Throw a clean exception to the user, preventing a 500 server crash
        throw new OrderServiceException("The Product Service is currently down or unreachable. We could not process your order at this time. Please try again later.");
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
            log.error("Failed to retrieve the orders: {}", e.getMessage());
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
            log.error("failed to find the Order By ID: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve order with id: {}", e.getMessage());
            throw new OrderServiceException("Failed to retrieve order with id: " + id, e);
        }
    }
}