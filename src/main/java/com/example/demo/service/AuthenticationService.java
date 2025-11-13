package com.example.demo.service;

import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Tenant;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repo.TenantRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TenantRepository tenantRepository;
    @PersistenceContext
    private EntityManager entityManager;

    // AuthenticationService.register
    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());

        UUID tenantId = null;

        if (registerRequest.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(registerRequest.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Tenant does not exist"));
            user.setTenant(tenant);
            tenantId = tenant.getId();

        } else if (registerRequest.getRole() == UserRole.ADMIN) {
            Tenant newTenant = new Tenant();
            newTenant.setName("Tenant-" + registerRequest.getUsername());
            newTenant.setEmail(registerRequest.getEmail());

            newTenant = tenantRepository.save(newTenant);
            tenantId = newTenant.getId();

            user.setTenant(newTenant);
            logger.info("New tenant created for admin: {}", registerRequest.getUsername());

        } else if (registerRequest.getRole() == UserRole.SHOPKEEPER) {
            throw new RuntimeException("Tenant ID is required for SHOPKEEPER role");
        }

        User savedUser = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .tenantId(tenantId)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        UUID tenantId = userRepository.findTenantIdByUserId(user.getId()).orElse(null);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .tenantId(tenantId)
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(jwtService.isTokenValid(refreshToken, userDetails)) {
            String newAccessToken = jwtService.generateToken(userDetails);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

            UUID tenantId = userRepository.findTenantIdByUserId(user.getId()).orElse(null);

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .tenantId(tenantId)
                    .build();
        }
        throw new RuntimeException("Invalid refresh token");
    }

}
