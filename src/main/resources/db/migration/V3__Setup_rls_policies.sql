-- Enable RLS on tables
ALTER TABLE books ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE tenants ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- Books policies
CREATE POLICY books_admin_policy ON books FOR ALL TO app_admin USING (true);

CREATE POLICY books_shopkeeper_policy ON books
FOR ALL TO app_shopkeeper
USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

CREATE POLICY books_customer_policy ON books
FOR SELECT TO app_customer
                             USING (is_available = true);

-- Orders policies
CREATE POLICY orders_admin_policy ON orders FOR ALL TO app_admin USING (true);

CREATE POLICY orders_customer_policy ON orders
FOR ALL TO app_customer
USING (customer_id = current_setting('app.current_user_id')::uuid);

-- Tenants policies
CREATE POLICY tenant_customer_policy ON tenants
FOR SELECT TO app_customer
                             USING (true);

-- Users policies
CREATE POLICY user_admin_policy ON users FOR ALL TO app_admin USING (true);

CREATE POLICY user_shopkeeper_policy ON users
FOR ALL TO app_shopkeeper
USING (tenant_id = current_setting('app.current_tenant_id')::uuid OR role = 'CUSTOMER');
