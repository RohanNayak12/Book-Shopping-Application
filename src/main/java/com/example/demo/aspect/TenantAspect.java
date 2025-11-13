package com.example.demo.aspect;

import com.example.demo.context.TenantContext;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class TenantAspect {

    @Autowired
    private EntityManager entityManager;

    @Before("execution(* com.example.demo.service.*.*(..))")
    public void applyRLS() {
        UUID tenantId = TenantContext.getTenantId();

        if (tenantId != null) {
            try{
                String sql=String.format("SET LOCAL app.current_tenant_id = '%s'",
                        tenantId.toString());
                entityManager.createNativeQuery(sql).executeUpdate();
            }
            catch (Exception e){
                throw new RuntimeException("Failed to set tenant context", e);
            }
        }
    }
}
