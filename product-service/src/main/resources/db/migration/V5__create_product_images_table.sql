-- V5: Create product_images table
-- Senior Note: Bảng quản lý hình ảnh sản phẩm

CREATE TABLE product_images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url VARCHAR(1000) NOT NULL,
    alt_text VARCHAR(255),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_primary BOOLEAN NOT NULL DEFAULT false
);

-- Indexes
CREATE INDEX idx_product_image_product ON product_images(product_id);
CREATE INDEX idx_product_image_primary ON product_images(is_primary) WHERE is_primary = true;
CREATE INDEX idx_product_image_order ON product_images(product_id, display_order);

-- Comments
COMMENT ON TABLE product_images IS 'Bảng hình ảnh sản phẩm';
COMMENT ON COLUMN product_images.is_primary IS 'Hình ảnh chính (thumbnail)';
COMMENT ON COLUMN product_images.display_order IS 'Thứ tự hiển thị';
