# Hướng Dẫn Test API Auth (identity-service)

Dưới đây là các lệnh `curl` để bạn test toàn bộ tính năng vừa triển khai.
**Lưu ý:** `identity-service` đang chạy trên port `8085`.

---

### 1. Đăng ký tài khoản mới (Register)
```bash
curl -X POST http://localhost:8085/api/v1/identity/register \
     -H "Content-Type: application/json" \
     -d '{
           "username": "testuser",
           "password": "Password123",
           "email": "test@example.com",
           "firstName": "Test",
           "lastName": "User"
         }'
```

---

### 2. Đăng nhập để lấy Token (Login)
Lệnh này sẽ trả về `accessToken` và `refreshToken`.
```bash
curl -X POST http://localhost:8085/api/v1/identity/login \
     -H "Content-Type: application/json" \
     -d '{
           "username": "testuser",
           "password": "Password123"
         }'
```

---

### 3. Làm mới Token (Refresh Token)
Dùng `refreshToken` nhận được từ bước 2.
```bash
curl -X POST http://localhost:8085/api/v1/identity/refresh \
     -H "Content-Type: application/json" \
     -d '{
           "refreshToken": "YOUR_REFRESH_TOKEN_HERE"
         }'
```

---

### 4. Cập nhật Profile (Yêu cầu Login)
Thay `YOUR_ACCESS_TOKEN` bằng token nhận được từ bước 2.
```bash
curl -X PUT http://localhost:8085/api/v1/identity/profile \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
           "firstName": "NewName",
           "lastName": "NewLast",
           "email": "updated@example.com"
         }'
```

---

### 5. Đổi mật khẩu (Yêu cầu Login)
```bash
curl -X PUT http://localhost:8085/api/v1/identity/password \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
           "oldPassword": "Password123",
           "newPassword": "NewPassword456"
         }'
```

---

### 6. Đăng xuất (Logout)
```bash
curl -X POST http://localhost:8085/api/v1/identity/logout \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

### 7. Phân quyền (Admin Only)
Endpoint này yêu cầu token phải có quyền `ADMIN`.
```bash
curl -X POST http://localhost:8085/api/v1/identity/roles/grant \
     -H "Authorization: Bearer ADMIN_ACCESS_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
           "userId": "TARGET_USER_ID",
           "roleName": "ADMIN"
         }'
```

---

### Mẹo nhỏ cho Senior:
1. Bạn có thể dùng **Postman** hoặc **Insomnia** để test cho trực quan hơn.
2. Để lấy `ADMIN_ACCESS_TOKEN`, bạn có thể login bằng tài khoản `admin/admin` (mặc định của Keycloak).
3. Đừng quên update `client-secret` trong `application.yml` khớp với giá trị trên Keycloak UI của bạn nhé!
