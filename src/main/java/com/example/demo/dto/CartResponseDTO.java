package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponseDTO {
    private UUID cartId;
    private UUID customerId;
    private List<CartItemDTO> items;
    private Integer totalItems;
    private BigDecimal totalPrice;
}
