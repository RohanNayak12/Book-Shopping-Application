-- Insert sample tenant
INSERT INTO tenants (id, name, email) VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'BookStore Inc', 'contact@bookstore.com');

-- Insert sample users
INSERT INTO users (id, tenant_id, username, email, password_hash, role) VALUES
                                                                            ('550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'admin', 'admin@bookstore.com', crypt('admin123', gen_salt('bf')), 'ADMIN'),
                                                                            ('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', 'shopkeeper1', 'shop@bookstore.com', crypt('shop123', gen_salt('bf')), 'SHOPKEEPER'),
                                                                            ('550e8400-e29b-41d4-a716-446655440003', NULL, 'customer1', 'customer@email.com', crypt('cust123', gen_salt('bf')), 'CUSTOMER');

-- Insert sample books
INSERT INTO books (tenant_id, title, author, price, stock_quantity) VALUES
                                                                        ('550e8400-e29b-41d4-a716-446655440000', 'Spring Boot in Action', 'Craig Walls', 45.99, 10),
                                                                        ('550e8400-e29b-41d4-a716-446655440000', 'Clean Code', 'Robert Martin', 39.99, 5);
