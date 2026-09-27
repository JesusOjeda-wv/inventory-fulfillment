CREATE TABLE orders (
                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        product_id BIGINT NOT NULL REFERENCES products(id),
                        quantity INTEGER NOT NULL CHECK (quantity > 0),
                        unit_price NUMERIC(10, 2) NOT NULL CHECK (unit_price >= 0),
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING', 'SHIPPED', 'CANCELLED')),
                        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);