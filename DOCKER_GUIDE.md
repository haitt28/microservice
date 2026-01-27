# Hướng dẫn chạy Microservices trên Windows với Docker

## Yêu cầu
- **Docker Desktop for Windows** (đã cài và đang chạy)
- **Java 21** (để build)
- **Maven 3.9+** (để build)

---

## Bước 1: Kiểm tra Docker Desktop

Mở PowerShell và chạy:
```powershell
docker --version
docker-compose --version
```

Nếu chưa cài, download tại: https://www.docker.com/products/docker-desktop/

---

## Bước 2: Build project với Maven

```powershell
cd C:\Users\Admin\Documents\Backend\microservice

# Build tất cả modules (bỏ qua tests để nhanh hơn)
mvn clean install -DskipTests
```

---

## Bước 3: Khởi động Infrastructure

```powershell
# Chạy các services cơ sở (Postgres, Redis, Kafka, Keycloak)
docker-compose up -d postgres redis zookeeper kafka keycloak

# Đợi khoảng 30-60 giây để services khởi động xong
# Kiểm tra logs:
docker-compose logs -f postgres keycloak
```

### Kiểm tra services đã sẵn sàng:
- PostgreSQL: `docker exec microservice-postgres pg_isready`
- Keycloak: http://localhost:8080 (admin/admin)
- Kafka UI: http://localhost:8090 (sau khi chạy kafka-ui)

---

## Bước 4: Chạy Business Services

### Option A: Chạy bằng IDE (recommended for development)
Mở mỗi service trong IntelliJ/Eclipse và run:
- `OrderServiceApplication` (port 8081)
- `InventoryServiceApplication` (port 8082)
- `PaymentServiceApplication` (port 8083)
- `NotificationServiceApplication` (port 8084)
- `IdentityServiceApplication` (port 8085)

### Option B: Chạy bằng Maven
```powershell
# Mở nhiều PowerShell windows

# Terminal 1 - Order Service
cd C:\Users\Admin\Documents\Backend\microservice\order-service
mvn spring-boot:run

# Terminal 2 - Identity Service
cd C:\Users\Admin\Documents\Backend\microservice\identity-service
mvn spring-boot:run
```

### Option C: Chạy ALL bằng Docker Compose
```powershell
# Build images và chạy tất cả
docker-compose up -d --build
```

---

## Bước 5: Test APIs

### 5.1 Đăng ký user mới
```powershell
curl -X POST http://localhost:8085/api/v1/users/register `
  -H "Content-Type: application/json" `
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123456",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 5.2 Lấy Access Token từ Keycloak
```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8080/realms/microservice-realm/protocol/openid-connect/token" `
  -Method POST `
  -Body @{
    client_id = "api-gateway"
    username = "user1"
    password = "user123"
    grant_type = "password"
  }

$token = $response.access_token
Write-Host "Token: $token"
```

### 5.3 Tạo Order
```powershell
curl -X POST http://localhost:8081/api/v1/orders `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token" `
  -d '{
    "items": [{
      "productId": "PROD-001",
      "productName": "iPhone 15 Pro",
      "quantity": 1,
      "unitPrice": 29990000
    }],
    "paymentMethod": "CREDIT_CARD"
  }'
```

---

## Bước 6: Monitoring

Sau khi chạy đầy đủ:
- **Keycloak Admin**: http://localhost:8080 (admin/admin)
- **Kafka UI**: http://localhost:8090
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Jaeger Tracing**: http://localhost:16686

---

## Troubleshooting

### Lỗi "port already in use"
```powershell
# Tìm process đang dùng port
netstat -ano | findstr :8080

# Kill process
taskkill /PID <PID> /F
```

### Lỗi Keycloak không khởi động
```powershell
# Check logs
docker-compose logs keycloak

# Restart
docker-compose restart keycloak
```

### Lỗi Database connection
```powershell
# Verify postgres đang chạy
docker-compose ps postgres

# Check database đã được tạo
docker exec -it microservice-postgres psql -U postgres -c "\l"
```

### Reset toàn bộ
```powershell
# Dừng và xóa tất cả containers + volumes
docker-compose down -v

# Khởi động lại từ đầu
docker-compose up -d
```

---

## Bước 7: Các lệnh Docker & Docker Compose thường dùng (Senior Cheat Sheet)

Dưới đây là các lệnh bạn sẽ thường xuyên sử dụng trong quá trình phát triển và vận hành hệ thống microservices này:

### 7.1 Quản lý Docker Compose (Lifecycle)
| Lệnh | Giải thích (Senior View) |
| :--- | :--- |
| `docker-compose up -d` | Khởi chạy tất cả services ở chế độ nền. Docker sẽ tự quản lý thứ tự chạy dựa trên `depends_on`. |
| `docker-compose up -d --build` | Ép Docker phải build lại image từ source code rồi mới khởi chạy. Dùng khi bạn vừa sửa code/cấu hình. |
| `docker-compose restart <name>` | Khởi động lại nhanh 1 service mà không phá hủy container. Dùng khi app bị treo. |
| `docker-compose stop` | Dừng các services nhưng KHÔNG xóa container. Trạng thái database và dữ liệu tạm vẫn được giữ nguyên. |
| `docker-compose down` | Dừng và **XÓA** sạch containers cùng với virtual network. |
| `docker-compose down -v` | Xóa cả **Volumes** (Dữ liệu database). Cực kỳ hữu ích khi bạn muốn reset DB về trạng thái trống hoàn toàn. |

### 7.2 Quản lý Volume (Dữ liệu bền vững)
Volume giúp dữ liệu (như Database) không bị mất khi container bị xóa.
| Lệnh | Giải thích |
| :--- | :--- |
| `docker volume ls` | Liệt kê tất cả các volumes đang có trên máy. |
| `docker volume inspect <name>` | Xem chi tiết Volume này đang nằm ở đâu trên ổ cứng máy thật của bạn. |
| `docker volume rm <name>` | Xóa thủ công một Volume không dùng tới. |
| `docker volume prune` | Dọn dẹp tất cả các Volumes "mồ côi" (không gắn với container nào). |

### 7.3 Quản lý Network (Kết nối nội bộ)
Dùng để debug khi các microservices không "nhìn thấy" nhau.
| Lệnh | Giải thích |
| :--- | :--- |
| `docker network ls` | Xem danh sách các mạng ảo (thường là `microservice-net`). |
| `docker network inspect <net>` | Xem danh sách IP của tất cả container đang tham gia vào mạng này. |
| `docker network connect <net> <c>`| Ép một container tham gia vào mạng mới mà không cần restart. |

### 7.4 Theo dõi Logs (Giám sát hệ thống)
| Lệnh | Giải thích |
| :--- | :--- |
| `docker-compose logs -f` | Theo dõi log của toàn bộ hệ thống theo thời gian thực. |
| `docker-compose logs -f <name>` | Chỉ theo dõi log của 1 service cụ thể (ví dụ: `order-service`). |
| `docker-compose logs --tail=100 <name>` | Xem 100 dòng log cuối cùng. |

### 7.5 Tương tác & Troubleshooting (Câu lệnh "Cứu hộ")
| Lệnh | Giải thích |
| :--- | :--- |
| `docker exec -it <name> sh` | Truy cập trực tiếp vào Terminal bên trong container để debug file hoặc môi trường. |
| `docker cp <host_path> <c_path>` | Copy file từ máy thật vào trong container (hoặc ngược lại). |
| `docker inspect <name>` | Xem toàn bộ cấu hình "ruột gan" của container (IP, Biến môi trường, Mount point). |
| `docker stats` | Xem mức độ "ngốn" RAM/CPU của từng microservice theo thời gian thực. |

### 7.6 Dọn dẹp hệ thống (System Maintenance)
Dùng khi máy bạn bị báo đầy ổ cứng do Docker chiếm dụng.
| Lệnh | Giải thích |
| :--- | :--- |
| `docker image prune` | Xóa các image "dangling" (image cũ bị đè sau khi build mới). |
| `docker container prune` | Xóa toàn bộ các container đã dừng (status: Exited). |
| `docker auto-clean (Senior)` | `docker system prune -a --volumes`: Lệnh "hủy diệt", xóa sạch SẠCH mọi thứ không dùng tới (Image, Volume, Network). |

> [!TIP]
> **Senior Tip**: Khi microservice báo lỗi `UnknownHostException` hoặc không nối được DB, hãy dùng `docker network inspect microservice-net`. Bạn sẽ thấy IP và Alias của từng service. Đôi khi restart Docker Desktop là cách giải quyết nhanh nhất cho các vấn đề network kỳ quái trên Windows.

---

## Bước 8: Giải thích cấu trúc Dockerfile (Senior Design Patterns)

Tất cả các Microservices trong dự án này đều sử dụng một mẫu Dockerfile chuẩn hóa, được tối ưu cho môi trường Production. Dưới đây là giải thích chi tiết các "mẫu thiết kế" (patterns) đã sử dụng:

### 8.1 Multi-stage Build (Xây dựng đa giai đoạn)
Chúng ta chia quá trình build làm 2 giai đoạn (`builder` và `runtime`):
- **Stage 1 (builder)**: Sử dụng JDK full để compile code.
- **Stage 2 (runtime)**: Chỉ sử dụng JRE (nhẹ hơn nhiều) để chạy app.
- **Lợi ích**: Giảm kích thước image cuối cùng, tăng tính bảo mật (không chứa source code hoặc công cụ build trong container vận hành).

### 8.2 Tối ưu Cache Layer (Dependency Caching)
Bằng cách `COPY mvnw`, `pom.xml` và chạy `go-offline` TRƯỚC khi copy source code:
- Docker sẽ cache lại toàn bộ thư viện Maven.
- Khi bạn sửa code Java, Docker sẽ KHÔNG tải lại thư viện, giúp tốc độ build tăng gấp 5-10 lần.

### 8.3 Layered JAR (Trích xuất lớp Spring Boot)
Lệnh `java -Djarmode=layertools -jar *.jar extract` chia file JAR thành 4 lớp:
1. `dependencies`: Các thư viện bên thứ 3 (ít thay đổi).
2. `spring-boot-loader`: Trình tải của Spring.
3. `snapshot-dependencies`: Các bản build tạm.
4. `application`: Code của bạn (thay đổi thường xuyên nhất).
- **Tại sao?**: Khi deploy, Docker chỉ cần đẩy lớp `application` siêu nhẹ qua mạng thay vì đẩy cả file JAR 100MB.

### 8.4 Bảo mật: Non-root User
Lệnh `USER appuser` cực kỳ quan trọng:
- Theo mặc định, Docker chạy quyền `root`. Nếu hacker chiếm được container, chúng sẽ có quyền root máy chủ.
- Chạy bằng `appuser` giúp giới hạn quyền hạn, bảo vệ máy chủ vật lý.

### 8.5 JVM Optimization (Tối ưu hiệu năng)
Các tham số trong `JAVA_OPTS`:
- `-XX:+UseZGC`: Sử dụng Garbage Collector thế hệ mới, giúp giảm độ trễ (latency) xuống mức cực thấp.
- `-XX:MaxRAMPercentage=75.0`: Tự động điều chỉnh RAM theo giới hạn của Docker (thay vì dùng `-Xmx` cứng nhắc).

### 8.6 Healthcheck (Xác thực trạng thái)
Docker sẽ định kỳ gọi vào `/actuator/health` của Spring Boot:
- Dự án sẽ biết chính xác khi nào một service bị "treo" để có biện pháp khởi động lại tự động (Self-healing).

> [!IMPORTANT]
> **Senior Note**: Đừng bao giờ tạo Dockerfile kiểu `COPY . .` rồi `RUN mvn package`. Đó là cách làm của Junior, khiến image nặng và build cực chậm!

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5432 | - |
| Redis | 6379 | - |
| Kafka | 9092 | - |
| Keycloak | 8080 | http://localhost:8080 |
| Kafka UI | 8090 | http://localhost:8090 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3000 | http://localhost:3000 |
| Jaeger | 16686 | http://localhost:16686 |
| Order Service | 8081 | http://localhost:8081 |
| Inventory Service | 8082 | http://localhost:8082 |
| Payment Service | 8083 | http://localhost:8083 |
| Notification Service | 8084 | http://localhost:8084 |
| Identity Service | 8085 | http://localhost:8085 |
