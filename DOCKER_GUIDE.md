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

## Services Ports Summary

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
