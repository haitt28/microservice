-- V4: Create product_variants table
-- Senior Note: Bảng quản lý biến thể sản phẩm với JSONB attributes

CREATE TABLE product_variants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) UNIQUE NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    sale_price DECIMAL(12,2),
    attributes JSONB,
    image_url VARCHAR(1000),
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Constraints
    CONSTRAINT chk_variant_price_positive CHECK (price >= 0),
    CONSTRAINT chk_variant_sale_price_positive CHECK (sale_price IS NULL OR sale_price >= 0),
    CONSTRAINT chk_variant_stock_positive CHECK (stock_quantity >= 0)
);

-- Indexes
CREATE INDEX idx_variant_product ON product_variants(product_id);
CREATE INDEX idx_variant_sku ON product_variants(sku);
CREATE INDEX idx_variant_available ON product_variants(available);

-- JSONB index cho query attributes
CREATE INDEX idx_variant_attributes ON product_variants USING gin(attributes);

-- Comments
COMMENT ON TABLE product_variants IS 'Bảng biến thể sản phẩm (màu sắc, size, v.v.)';
COMMENT ON COLUMN product_variants.attributes IS 'Thuộc tính dạng JSON: {"color": "Red", "size": "XL"}';
COMMENT ON COLUMN product_variants.stock_quantity IS 'Tồn kho (denormalized từ Inventory Service)';
