package org.deloitte.onlineorderapp.dto;


import org.deloitte.onlineorderapp.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String customerName,
        String customerEmail,
        Long productId,
        Integer quantity,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt
) {}