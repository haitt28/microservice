-- V2: Create categories table
-- Senior Note: Self-referencing table cho hierarchical categories

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    parent_id UUID REFERENCES categories(id) ON DELETE CASCADE,
    image_url VARCHAR(500),
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    product_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes
CREATE INDEX idx_category_slug ON categories(slug);
CREATE INDEX idx_category_parent ON categories(parent_id);
CREATE INDEX idx_category_active ON categories(active);
CREATE INDEX idx_category_display_order ON categories(display_order);

-- Comments
COMMENT ON TABLE categories IS 'Bảng quản lý danh mục sản phẩm (hierarchical)';
COMMENT ON COLUMN categories.parent_id IS 'ID của danh mục cha (NULL = root category)';
COMMENT ON COLUMN categories.product_count IS 'Số lượng sản phẩm trong danh mục';
