-- Create application roles
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'app_admin') THEN
CREATE ROLE app_admin WITH LOGIN PASSWORD 'admin_secure_password';
END IF;

    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'app_shopkeeper') THEN
CREATE ROLE app_shopkeeper WITH LOGIN PASSWORD 'shopkeeper_secure_password';
END IF;

    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'app_customer') THEN
CREATE ROLE app_customer WITH LOGIN PASSWORD 'customer_secure_password';
END IF;
END
$$;

-- Grant permissions
GRANT ALL ON ALL TABLES IN SCHEMA public TO app_admin;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO app_shopkeeper;
GRANT SELECT, INSERT ON orders TO app_customer;
GRANT SELECT ON books TO app_customer;
GRANT SELECT ON tenants TO app_customer;
