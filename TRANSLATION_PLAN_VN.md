# Dịch Chú thích Code sang Tiếng Việt

Dịch tất cả các chú thích (comments) tiếng Anh trong mã nguồn sang tiếng Việt, đồng thời giữ nguyên các thuật ngữ chuyên ngành và duy trì phong cách của một lập trình viên cao cấp (senior developer).

## Các Thay đổi Đề xuất

### Thư viện Chung (`common-lib`)
- Quét tất cả các tệp trong `src/main/java/com/fiinx/common` để tìm các chú thích tiếng Anh.
- Dịch các chú thích trong annotation, entity, exception và các lớp tiện ích (utility classes).

### Dịch vụ Định danh (`identity-service`)
- Kiểm tra `IdentityService.java`, `SecurityConfig.java` và các tệp khác trong gói `api` và `application`.
- Đảm bảo tất cả các chú thích đều bằng tiếng Việt với văn phong chuyên nghiệp.

### Dịch vụ Sản phẩm (`product-service`)
- Dịch các chú thích tiếng Anh còn lại trong `ProductService.java`.
- Quét và dịch các chú thích trong các gói `controller`, `repository` và `domain`.

### Dịch vụ Đơn hàng (`order-service`)
- Dịch các chú thích tiếng Anh trong `OrderService.java`.
- Quét và dịch các chú thích trong các gói `saga`, `repository` và `domain`.

### Các Microservice Khác
- Hệ thống quét và dịch các chú thích trong các dịch vụ sau:
  - `review-service`
  - `cart-service`
  - `promotion-service`
  - `inventory-service`
  - `notification-service`
  - `payment-service`
  - `shipping-service`
  - `wishlist-service`

### Ứng dụng Frontend (`frontend`)
- Quét thư mục `src` cho tất cả các tệp `.ts`, `.tsx`.
- Dịch các chú thích trong:
  - `middleware.ts` (Logic Auth)
  - `src/lib/api.ts` (Cấu hình Axios/Interceptors)
  - `src/services` (Các lớp dịch vụ API)
  - `src/components` (Các UI Components)
  - `src/app` (Các trang và layout của Next.js)
  - `src/store` (Quản lý State)
- Giữ nguyên các thuật ngữ kỹ thuật bằng tiếng Anh (ví dụ: "Next.js", "React", "Axios", "Interceptor", "Middleware", "SSR", "CSR", "Hydration", "Auth", "Session").

## Kế hoạch Xác minh

### Xác minh Thủ công
- Kiểm tra từng tệp đã dịch để đảm bảo:
  - Không có thay đổi logic nào được đưa vào.
  - Các chú thích rõ ràng và chính xác bằng tiếng Việt.
  - Các thuật ngữ chuyên ngành (ví dụ: "Saga", "Kafka", "Redis", "JWT", "Spring Security", "Optimistic Locking") vẫn giữ nguyên tiếng Anh.
  - Văn phong chuyên nghiệp và "senior".
- Biên dịch dự án để đảm bảo không có lỗi cú pháp nào phát sinh do quá trình dịch.
