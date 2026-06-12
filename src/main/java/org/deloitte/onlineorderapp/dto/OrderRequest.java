package org.deloitte.onlineorderapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

//public record OrderRequest(
//        //@NotBlank(message = "Customer name is required") String customerName,
//      //  @Email(message = "Customer email is invalid") @NotBlank(message = "Customer email is required") String customerEmail,
//        @NotNull(message = "Product id is required") Long productId,
//        @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity
//) {}

@Data
public  class OrderRequest{
    @NotNull(message = "Product id is required")
    Long productId;

    @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;
}
