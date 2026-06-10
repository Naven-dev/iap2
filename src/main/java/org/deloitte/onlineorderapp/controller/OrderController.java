package org.deloitte.onlineorderapp.controller;


import lombok.RequiredArgsConstructor;
import org.deloitte.onlineorderapp.dto.OrderRequest;
import org.deloitte.onlineorderapp.dto.OrderResponse;
import org.deloitte.onlineorderapp.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor

public class OrderController{

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/id")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }


}
