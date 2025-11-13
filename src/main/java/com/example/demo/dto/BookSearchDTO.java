package com.example.demo.dto;


import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchDTO {
    private String title;
    private String author;
    private String category;

    @DecimalMin(value = "0.0",inclusive = false)
    private Double minPrice;

    @DecimalMin(value = "0.0",inclusive = false)
    private Double maxPrice;

    private boolean isAvailable;
}
