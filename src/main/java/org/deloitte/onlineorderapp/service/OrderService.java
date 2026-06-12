package org.deloitte.onlineorderapp.service;

import org.deloitte.onlineorderapp.dto.OrderRequest;
import org.deloitte.onlineorderapp.dto.OrderResponse;

import java.util.List;



public interface OrderService {
   // OrderResponse createOrder(Long productId, Integer quantity);

    OrderResponse createOrder(OrderRequest orderRequest);

    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
}
