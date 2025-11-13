package com.example.demo.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TenantWithAdminRegistrationRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String tenantName;

    @NotBlank
    @Email
    private String tenantEmail;

    // First admin info
    @NotBlank
    @Size(min = 3, max = 50)
    private String adminUsername;

    @NotBlank
    @Email
    private String adminEmail;

    @NotBlank
    @Size(min = 6)
    private String adminPassword;
}
