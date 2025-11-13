-- Add new columns to orders table
ALTER TABLE orders ADD COLUMN IF NOT EXISTS unit_price DECIMAL(10, 2);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS cancellation_reason TEXT;

-- Update status column to use ENUM
ALTER TABLE orders ALTER COLUMN status TYPE VARCHAR(50);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_date ON orders(order_date DESC);

-- Update RLS policies for orders
DROP POLICY IF EXISTS orders_customer_policy ON orders;

CREATE POLICY orders_customer_policy ON orders
FOR ALL TO app_customer
USING (customer_id = (SELECT id FROM users WHERE username = current_user));

-- Allow shopkeepers to view orders for their books
CREATE POLICY orders_shopkeeper_policy ON orders
FOR SELECT TO app_shopkeeper
                           USING (book_id IN (
                           SELECT id FROM books WHERE tenant_id = current_setting('app.current_tenant_id')::uuid
                           ));
