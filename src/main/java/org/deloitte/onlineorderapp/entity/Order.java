package org.deloitte.onlineorderapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;
}
