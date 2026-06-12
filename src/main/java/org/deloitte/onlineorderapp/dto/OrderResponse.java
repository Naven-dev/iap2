package org.deloitte.onlineorderapp.dto;


import lombok.Data;
import org.deloitte.onlineorderapp.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//public record OrderResponse(
//        Long id,
//       // String customerName,
//        //String customerEmail,
//        Long productId,
//        Integer quantity,
//        BigDecimal totalPrice,
//        OrderStatus status,
//        LocalDateTime createdAt
//) {}
@Data
public class OrderResponse{
    Long id;
    Long productId;
       Integer quantity;
    BigDecimal totalPrice;
    OrderStatus status;
      LocalDateTime createdAt;

}