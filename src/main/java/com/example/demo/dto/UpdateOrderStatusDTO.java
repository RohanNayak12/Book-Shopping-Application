package com.example.demo.dto;

import com.example.demo.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusDTO {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    private String reason;
}
