-- Performance indexes
CREATE INDEX idx_books_tenant_id ON books(tenant_id);
CREATE INDEX idx_books_available ON books(is_available);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_users_tenant_id ON users(tenant_id);
CREATE INDEX idx_users_role ON users(role);
