-- V1: Create brands table
-- Senior Note: Tạo bảng brands trước vì products có foreign key tới brands

CREATE TABLE brands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    logo_url VARCHAR(500),
    website_url VARCHAR(500),
    country VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    product_count INTEGER NOT NULL DEFAULT 0,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes for performance
CREATE INDEX idx_brand_slug ON brands(slug);
CREATE INDEX idx_brand_active ON brands(active);
CREATE INDEX idx_brand_display_order ON brands(display_order);

-- Comments for documentation
COMMENT ON TABLE brands IS 'Bảng quản lý thương hiệu sản phẩm';
COMMENT ON COLUMN brands.slug IS 'SEO-friendly URL slug';
COMMENT ON COLUMN brands.product_count IS 'Số lượng sản phẩm (denormalized)';
COMMENT ON COLUMN brands.version IS 'Optimistic locking version';
