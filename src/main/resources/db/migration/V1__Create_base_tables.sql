-- DO NOT create extensions here - already created in Step 1

-- Tenants table
CREATE TABLE tenants (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL,
                       password_hash TEXT NOT NULL,
                       role VARCHAR(50) NOT NULL CHECK (role IN ('SHOPKEEPER', 'CUSTOMER', 'ADMIN')),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books table
CREATE TABLE books (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255),
                       price DECIMAL(10, 2) NOT NULL,
                       stock_quantity INT NOT NULL DEFAULT 0,
                       is_available BOOLEAN DEFAULT true,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        customer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        book_id UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
                        quantity INT NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(50) DEFAULT 'PENDING'
);
