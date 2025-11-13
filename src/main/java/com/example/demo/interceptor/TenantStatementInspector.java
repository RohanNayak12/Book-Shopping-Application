package com.example.demo.interceptor;

import com.example.demo.context.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.internal.EmptyInterceptor;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantStatementInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
//        UUID tenantId = TenantContext.getTenantId();
//
//        // If tenant ID is set, prepend the SET LOCAL command
//        if (tenantId != null) {
//            return String.format("SET LOCAL app.current_tenant_id = %s; %s", tenantId, sql);
//        }

        return sql;
    }
}