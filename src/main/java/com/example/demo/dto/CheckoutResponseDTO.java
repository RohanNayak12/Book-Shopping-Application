package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponseDTO {
    private List<OrderResponseDTO> orders;
    private Integer totalOrders;
    private BigDecimal totalAmount;
    private String message;
}
