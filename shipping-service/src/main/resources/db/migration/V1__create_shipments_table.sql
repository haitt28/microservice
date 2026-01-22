-- V1: Create shipments table

CREATE TABLE shipments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    provider VARCHAR(50) NOT NULL,
    tracking_code VARCHAR(100) UNIQUE,
    sender_address TEXT NOT NULL,
    receiver_address TEXT NOT NULL,
    weight DECIMAL(10,2),
    shipping_fee DECIMAL(12,2),
    status VARCHAR(50) NOT NULL,
    estimated_delivery TIMESTAMP,
    actual_delivery TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_shipment_order ON shipments(order_id);
CREATE INDEX idx_shipment_tracking ON shipments(tracking_code);
CREATE INDEX idx_shipment_status ON shipments(status);
