CREATE TABLE payments (
    id UUID PRIMARY KEY,
    order_id VARCHAR(100) NOT NULL UNIQUE,
    customer_id VARCHAR(100) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_method VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    transaction_id VARCHAR(100),
    idempotency_key VARCHAR(200) UNIQUE,
    failure_reason VARCHAR(500),
    refund_reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    refunded_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_payment_order ON payments(order_id);
CREATE INDEX idx_payment_idempotency ON payments(idempotency_key);
