package com.example.demo.config;

import com.example.demo.interceptor.TenantStatementInspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
public class HibernateConfig implements HibernatePropertiesCustomizer {

    @Autowired
    private TenantStatementInspector tenantStatementInspector;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(
                "hibernate.session_factory.statement_inspector",
                tenantStatementInspector
        );
    }

}