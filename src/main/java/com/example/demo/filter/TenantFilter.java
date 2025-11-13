package com.example.demo.filter;

import com.example.demo.context.TenantContext;
import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(2)
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Lazy
    @Autowired
    private UserRepository userRepository;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String path=httpRequest.getRequestURI();
            if(path.startsWith("/api/auth/")){
                chain.doFilter(request,response);
                return;
            }
            String tenantIdHeader = httpRequest.getHeader(TENANT_HEADER);

            if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
                try {
                    UUID tenantId = UUID.fromString(tenantIdHeader);
                    TenantContext.setTenantId(tenantId);
                } catch (IllegalArgumentException e) {
                    httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    httpResponse.getWriter().write("{\"error\": \"Invalid tenant ID format\"}");
                    return;
                }
            } else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if(auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                    String username=auth.getName();
                    User user= userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
                    if (user.getTenant() != null) {
                        TenantContext.setTenantId(user.getTenant().getId());
                    }
                }
            }
            chain.doFilter(request,response);
        }
        finally {
            TenantContext.clear();
        }
    }
}
