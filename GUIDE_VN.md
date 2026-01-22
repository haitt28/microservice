# Hướng Dẫn Kỹ Thuật & Tổng Kết Hệ Thống Microservices (Senior Level)

Chào bạn, tôi đã thực hiện một đợt "tổng vệ sinh" và hiện đại hóa lại toàn bộ hệ thống để đảm bảo code chạy mượt mà, đúng chuẩn production. Dưới đây là tổng hợp các vấn đề chúng ta đã xử lý và hướng dẫn chi tiết để bạn làm chủ dự án này.

---

## 1. Tổng Hợp Các Lỗi Đã Xử Lý (Error Summary)

Trong quá trình khởi chạy, chúng ta đã gặp 6 nhóm lỗi chiến lược. Tôi đã fix chúng theo tư duy "Senior":

| STT | Lỗi Gặp Phải | Nguyên Nhân | Giải Pháp (Senior Touch) |
| :--- | :--- | :--- | :--- |
| 1 | **Missing Table (Postgres)** | `inventory-service` và `payment-service` thiếu bảng dữ liệu khi khởi chạy. | Thêm các file Flyway Migration (`V1__...sql`) thay vì dùng `@DynamicUpdate`. Giúp quản lý cấu trúc DB chuyên nghiệp. |
| 2 | **NoClassDefFoundError: Tracer** | `GlobalExceptionHandler` dùng `Tracer` để trace log nhưng thiếu thư viện Micrometer Tracing. | Bổ sung `micrometer-tracing-bridge-otel` vào `pom.xml` của các service. Đồng thời fix code inject `Tracer` để tránh lỗi Proxy. |
| 3 | **Eureka Connection Refused** | Eureka là công nghệ cũ, gây chậm startup và thường xuyên lỗi kết nối Localhost trên Windows. | **Loại bỏ hoàn toàn Eureka**. Chuyển sang dùng **Docker DNS**. Các service gọi nhau qua tên (ví dụ: `http://order-service:8081`). |
| 4 | **Redis ERR AUTH** | `order-service` bị lỗi pass Redis dù chúng ta chưa set pass cho Docker Redis. | Xóa cấu hình `password` trong `application.yml`. Redisson sẽ tự hiểu là kết nối không mật khẩu. |
| 5 | **Redis Lua Script Error** | Lỗi `tonumber` trong script Rate Limit vì truyền dữ liệu kiểu Byte thay vì String. | Update `RateLimitAspect.java` sử dụng `StringCodec`. Ép kiểu dữ liệu sang String trước khi gửi xuống Redis. |
| 6 | **Resilience4j Conflict** | Lỗi cấu hình Retry 2 lần trong `order-service`. | Xóa `waitDuration` dư thừa, chỉ giữ lại `intervalFunction` (hàm mũ) để backoff chuyên nghiệp hơn. |

---

## 2. Tổng Hợp Các Lệnh Docker & Ý Nghĩa

Tôi đã tối ưu `docker-compose.yml` để bạn chạy **Infrastructure** (DB, Kafka, Redis...) trên Docker, còn **Code Business** chạy trên IDE để dễ debug.

### Các lệnh quan trọng:

1.  **Khởi động hạ tầng:**
    ```powershell
    docker-compose up -d
    ```
    *   **Công dụng:** Tải image, tạo network và chạy các container ở chế độ chạy ngầm (`-d`).
    *   **Giải thích:** Nó sẽ dựng lên Postgres, Redis, Kafka, Keycloak, Prometheus, Grafana và Jaeger. Đây là "nền móng" để app của bạn sống được.

2.  **Dừng và dọn dẹp:**
    ```powershell
    docker-compose down
    ```
    *   **Công dụng:** Dừng tất cả container và xóa network ảo.
    *   **Giải thích:** Dùng khi bạn muốn reset lại môi trường sạch sẽ.

3.  **Xem log của một service (ví dụ Postgres):**
    ```powershell
    docker logs -f postgres
    ```
    *   **Công dụng:** Theo dõi log trực tiếp (`-f` là follow).
    *   **Giải thích:** Rất quan trọng khi bạn nghi ngờ DB không lên hoặc bị lỗi port.

---

## 3. Quy Trình Chạy Project (Step-by-Step)

Bạn hãy làm đúng theo thứ tự này để tránh lỗi "race condition" (cái này chạy trước cái kia chưa kịp lên):

### Bước 1: Build Source Code
Mở Terminal tại thư mục gốc `microservice/` và chạy:
```powershell
mvn clean install -DskipTests
```
*Ghi chú:* Việc build này để Maven download đủ các thư viện mới tôi vừa thêm vào và compile code `common-lib`.

### Bước 2: Chạy Infrastructure (Docker)
```powershell
docker-compose up -d
```
Đợi khoảng 30s-1 phút để Keycloak và Kafka khởi động hoàn tất.

### Bước 3: Chạy Microservices (Trên IDE - IntelliJ/Eclipse)
Hãy chạy theo thứ tự ưu tiên sau:
1.  **Identity Service**: Cung cấp Auth.
2.  **Inventory Service**: Quản lý kho.
3.  **Payment Service**: Thanh toán.
4.  **Order Service**: Xử lý đơn hàng (Service này gọi các service trên).
5.  **Notification Service**: Gửi thông báo (Lắng nghe Kafka).

---

## 4. Hệ Thống Định Danh Nâng Cao (Advanced Identity)

Tôi đã nâng cấp `identity-service` từ một bản demo đơn giản thành một hệ thống **Identity Manager** thực thụ. Các tính năng mới bao gồm:

### Các Endpoint mới (`/api/v1/identity/...`):
1.  **Login**: Trao đổi `username/password` lấy Access Token & Refresh Token.
2.  **Refresh**: Lấy token mới khi token cũ hết hạn mà không cần login lại.
3.  **Logout**: Hủy session của người dùng trên Keycloak.
4.  **Update Profile**: Cho phép user tự đổi Họ, Tên, Email.
5.  **Change Password**: Cơ chế đổi mật khẩu an toàn.
6.  **Grant Role (Admin Only)**: API dành riêng cho Admin để phân quyền cho người dùng (ví dụ gán quyền `ADMIN`).

### Cấu Hình "Senior" trong Security:
-   **Role Mapping**: Tôi đã viết `JwtAuthenticationConverter` để tự động map các Role từ Keycloak (trong `realm_access.roles`) vào Spring Security. Bạn có thể dùng `@PreAuthorize("hasRole('ADMIN')")` một cách dễ dàng.
-   **Default Role**: Khi một user đăng ký mới, hệ thống sẽ tự động gán role `USER`.

---

## 5. Giải Thích Code "Senior Style" (Sơ lược)

Tôi đã comment tiếng Việt vào code, bạn sẽ thấy các ghi chú như:
*   **RateLimitAspect.java**: Giải thích cách dùng Lua Script để đảm bảo tính *Atomic* (không bị tranh chấp khi nhiều request đến cùng lúc).
*   **GlobalExceptionHandler.java**: Cách trích xuất `traceId` để khi app lỗi, bạn có thể bê cái ID đó lên Jaeger để tìm đúng dòng code bị chết.
*   **ResilienceConfig.java**: Giải thích về chiến thuật "Exponential Backoff" - không nên retry liên tục ngay lập tức mà nên tăng dần thời gian chờ để "tha cho" service đang bị quá tải.

> [!TIP]
> **Lời khuyên từ Senior**: Hãy luôn kiểm tra file `application.yml`. Trong môi trường local, tôi đã đổi `localhost` thành `127.0.0.1` để tránh lỗi phân giải IPv6 của Windows (một lỗi cực kỳ gây ức chế).
