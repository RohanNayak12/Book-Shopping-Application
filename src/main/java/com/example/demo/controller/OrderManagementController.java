package com.example.demo.controller;


import com.example.demo.dto.OrderResponseDTO;
import com.example.demo.dto.UpdateOrderStatusDTO;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasAnyRole('ADMIN','SHOPKEEPER')")
public class OrderManagementController {
    @Autowired
    private OrderService orderService;

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Valid @RequestBody UpdateOrderStatusDTO  updateOrderStatusDTO,
            @PathVariable UUID orderId
    ) throws Exception{
        OrderResponseDTO orderResponseDTO=orderService.updateOrderStatus(orderId,updateOrderStatusDTO);
        return ResponseEntity.ok(orderResponseDTO);
    }
}
