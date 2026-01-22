-- V2: Create coupon_usage table
CREATE TABLE coupon_usage (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coupon_id UUID NOT NULL REFERENCES coupons(id),
    user_id VARCHAR(255) NOT NULL,
    order_id UUID,
    discount_amount DECIMAL(12,2) NOT NULL,
    used_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usage_coupon ON coupon_usage(coupon_id);
CREATE INDEX idx_usage_user ON coupon_usage(user_id);
CREATE INDEX idx_usage_order ON coupon_usage(order_id);
