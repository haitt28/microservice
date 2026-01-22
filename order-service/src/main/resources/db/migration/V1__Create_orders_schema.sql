-- ============================================================
-- BEST PRACTICE #30: Database Migration with Flyway
-- ============================================================
-- V1__Create_orders_schema.sql
-- Initial schema for orders and order_items tables
-- ============================================================

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Order identification
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id VARCHAR(100) NOT NULL,
    customer_email VARCHAR(255),
    
    -- Status
    status VARCHAR(30) NOT NULL,
    
    -- Amounts
    subtotal DECIMAL(15, 2) NOT NULL,
    tax_amount DECIMAL(15, 2) DEFAULT 0,
    shipping_amount DECIMAL(15, 2) DEFAULT 0,
    discount_amount DECIMAL(15, 2) DEFAULT 0,
    total_amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'VND',
    
    -- Shipping address (embedded)
    recipient_name VARCHAR(200),
    phone VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(2),
    
    -- Payment
    payment_method VARCHAR(50),
    payment_id VARCHAR(100),
    reservation_id VARCHAR(100),
    
    -- Additional info
    notes VARCHAR(1000),
    failure_reason VARCHAR(500),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    
    -- Audit
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Soft delete
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Foreign key
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    -- Product info
    product_id VARCHAR(100) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(100),
    
    -- Pricing
    quantity INT NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    discount_amount DECIMAL(15, 2) DEFAULT 0,
    subtotal DECIMAL(15, 2) NOT NULL,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    
    -- Audit
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Soft delete
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- ============================================================
-- Indexes for performance
-- ============================================================

CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_customer_status ON orders(customer_id, status) WHERE deleted = FALSE;

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- ============================================================
-- Outbox table for reliable event publishing
-- ============================================================

CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    
    -- Aggregate info
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    
    -- Event info
    event_type VARCHAR(200) NOT NULL,
    payload JSONB NOT NULL,
    
    -- Kafka info
    topic VARCHAR(200) NOT NULL,
    partition_key VARCHAR(100),
    
    -- Processing status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    correlation_id VARCHAR(100),
    
    -- Retry tracking
    retry_count INT NOT NULL DEFAULT 0,
    last_error VARCHAR(2000),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    published_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_outbox_status ON outbox_events(status);
CREATE INDEX idx_outbox_created_at ON outbox_events(created_at);
CREATE INDEX idx_outbox_aggregate ON outbox_events(aggregate_type, aggregate_id);

-- ============================================================
-- Comments for documentation
-- ============================================================

COMMENT ON TABLE orders IS 'Main orders table storing order header information';
COMMENT ON TABLE order_items IS 'Order line items with product and pricing details';
COMMENT ON TABLE outbox_events IS 'Transactional outbox for reliable event publishing';

COMMENT ON COLUMN orders.status IS 'Order status: CREATED, PENDING_INVENTORY, INVENTORY_RESERVED, PENDING_PAYMENT, PAYMENT_PROCESSED, COMPLETED, FAILED, CANCELLED';
COMMENT ON COLUMN outbox_events.status IS 'Event publishing status: PENDING, PUBLISHED, FAILED, SKIPPED';
