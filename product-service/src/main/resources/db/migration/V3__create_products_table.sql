-- V3: Create products table
-- Senior Note: Main products table với nhiều indexes cho performance

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(500) NOT NULL,
    slug VARCHAR(500) UNIQUE NOT NULL,
    description TEXT,
    short_description VARCHAR(1000),
    base_price DECIMAL(12,2) NOT NULL,
    sale_price DECIMAL(12,2),
    sku VARCHAR(100) UNIQUE NOT NULL,
    brand_id UUID REFERENCES brands(id) ON DELETE SET NULL,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    view_count INTEGER NOT NULL DEFAULT 0,
    sold_count INTEGER NOT NULL DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    review_count INTEGER NOT NULL DEFAULT 0,
    featured BOOLEAN NOT NULL DEFAULT false,
    new_arrival BOOLEAN NOT NULL DEFAULT false,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Constraints
    CONSTRAINT chk_base_price_positive CHECK (base_price >= 0),
    CONSTRAINT chk_sale_price_positive CHECK (sale_price IS NULL OR sale_price >= 0),
    CONSTRAINT chk_average_rating_range CHECK (average_rating >= 0 AND average_rating <= 5),
    CONSTRAINT chk_status_valid CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'OUT_OF_STOCK', 'DELETED'))
);

-- Indexes for performance
CREATE INDEX idx_product_slug ON products(slug);
CREATE INDEX idx_product_sku ON products(sku);
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_brand ON products(brand_id);
CREATE INDEX idx_product_status ON products(status);
CREATE INDEX idx_product_featured ON products(featured) WHERE featured = true;
CREATE INDEX idx_product_new_arrival ON products(new_arrival) WHERE new_arrival = true;
CREATE INDEX idx_product_published ON products(published_at);
CREATE INDEX idx_product_base_price ON products(base_price);
CREATE INDEX idx_product_sold_count ON products(sold_count DESC);
CREATE INDEX idx_product_rating ON products(average_rating DESC);

-- Full-text search index (for simple search, ElasticSearch cho advanced search)
CREATE INDEX idx_product_name_trgm ON products USING gin(name gin_trgm_ops);
CREATE INDEX idx_product_description_trgm ON products USING gin(description gin_trgm_ops);

-- Comments
COMMENT ON TABLE products IS 'Bảng sản phẩm chính (Aggregate Root)';
COMMENT ON COLUMN products.slug IS 'SEO-friendly URL';
COMMENT ON COLUMN products.sku IS 'Stock Keeping Unit - mã sản phẩm';
COMMENT ON COLUMN products.status IS 'DRAFT | ACTIVE | INACTIVE | OUT_OF_STOCK | DELETED';
COMMENT ON COLUMN products.view_count IS 'Số lượt xem (denormalized)';
COMMENT ON COLUMN products.sold_count IS 'Số lượng đã bán (denormalized từ Order Service)';
COMMENT ON COLUMN products.average_rating IS 'Điểm đánh giá trung bình (denormalized từ Review Service)';
