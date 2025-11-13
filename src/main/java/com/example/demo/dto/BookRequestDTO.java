package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 255, message = "Author name must not exceed 255 characters")
    private String author;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01",message = "Price must be greater than 0")
    @Digits(integer = 8,fraction = 2,message = "Price format is invalid")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0,message = "Stock cannot be less than 0")
    private Integer stockQuantity;

    private boolean isAvailable=true;
}
