package org.deloitte.onlineorderapp.config;

import org.deloitte.onlineorderapp.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name ="PRODUCT-SERVICE", url="http://product-service:8081/api")
public interface ProductClient {
    @GetMapping("/products/{id}")
   ProductResponse getProduct(@PathVariable Long id);
}
