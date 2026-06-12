package org.deloitte.onlineorderapp.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Double price;
    private Integer stockQuantity;
}
