package org.deloitte.onlineorderapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.deloitte.onlineorderapp.dto.OrderRequest;
import org.deloitte.onlineorderapp.dto.OrderResponse;
import org.deloitte.onlineorderapp.exception.GlobalExceptionHandler;
import org.deloitte.onlineorderapp.exception.OrderServiceException;
import org.deloitte.onlineorderapp.service.OrderService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private OrderService orderService;

    @Test
    void createOrder_returnsCreated() throws Exception {
        OrderRequest req = new OrderRequest();
        req.setProductId(1L);
        req.setQuantity(2);

        OrderResponse resp = new OrderResponse();
        resp.setId(10L);
        resp.setProductId(1L);
        resp.setQuantity(2);
        resp.setTotalPrice(BigDecimal.valueOf(20.0));

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void getAllOrders_returnsList() throws Exception {
        OrderResponse r = new OrderResponse();
        r.setId(1L);
        r.setProductId(2L);
        r.setQuantity(3);

        when(orderService.getAllOrders()).thenReturn(List.of(r));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getOrderById_returnsOrder() throws Exception {
        OrderResponse r = new OrderResponse();
        r.setId(5L);
        r.setProductId(7L);
        r.setQuantity(2);

        when(orderService.getOrderById(5L)).thenReturn(r);

        mockMvc.perform(get("/api/orders/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.productId").value(7));
    }

    @Test
    void getOrderById_whenServiceThrowsOrderServiceException_returnsInternalServerError() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new OrderServiceException("not found"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("not found"));
    }
}

