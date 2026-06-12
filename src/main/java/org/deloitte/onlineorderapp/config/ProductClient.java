package org.deloitte.onlineorderapp.config;

import org.deloitte.onlineorderapp.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name ="PRODUCT-SERVICE")
public interface ProductClient {
    @GetMapping("/products/{id}")
   ProductResponse getProduct(@PathVariable Long id);
}
