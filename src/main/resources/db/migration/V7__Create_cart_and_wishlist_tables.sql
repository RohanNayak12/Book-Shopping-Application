-- Add category column to books table
ALTER TABLE books ADD COLUMN IF NOT EXISTS category VARCHAR(100);

-- Create shopping_carts table
CREATE TABLE shopping_carts (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                customer_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create cart_items table
CREATE TABLE cart_items (
                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            cart_id UUID NOT NULL REFERENCES shopping_carts(id) ON DELETE CASCADE,
                            book_id UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
                            quantity INT NOT NULL CHECK (quantity > 0),
                            unit_price DECIMAL(10, 2) NOT NULL,
                            added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create wishlists table
CREATE TABLE wishlists (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           customer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           book_id UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
                           added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE(customer_id, book_id)
);

-- Create indexes
CREATE INDEX idx_cart_customer_id ON shopping_carts(customer_id);
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_book_id ON cart_items(book_id);
CREATE INDEX idx_wishlist_customer_id ON wishlists(customer_id);
CREATE INDEX idx_wishlist_book_id ON wishlists(book_id);
CREATE INDEX idx_books_category ON books(category);

-- Enable RLS on new tables
ALTER TABLE shopping_carts ENABLE ROW LEVEL SECURITY;
ALTER TABLE cart_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE wishlists ENABLE ROW LEVEL SECURITY;

-- RLS policies for shopping_carts
CREATE POLICY cart_customer_policy ON shopping_carts
FOR ALL TO app_customer
USING (customer_id = current_setting('app.current_user_id')::uuid);

-- RLS policies for cart_items
CREATE POLICY cart_items_customer_policy ON cart_items
FOR ALL TO app_customer
USING (cart_id IN (SELECT id FROM shopping_carts WHERE customer_id = current_setting('app.current_user_id')::uuid));

-- RLS policies for wishlists
CREATE POLICY wishlist_customer_policy ON wishlists
FOR ALL TO app_customer
USING (customer_id = current_setting('app.current_user_id')::uuid);
