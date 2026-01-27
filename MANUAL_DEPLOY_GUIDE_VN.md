# Hướng dẫn Triển khai Thủ công qua SSH/SCP (Manual Deployment)

Chào bạn, đây là hướng dẫn "kéo tay" source code lên server để deploy trong trường hợp bạn chưa kịp cấu hình CI/CD hoặc pipeline tự động gặp sự cố.

---

## 1. Các bước chuẩn bị trên máy cá nhân (Local)

Trước khi gửi code lên server, chúng ta cần "thu dọn" để tránh gửi các folder nặng và không cần thiết.

### 1.1 Nén source code (Khuyên dùng)
Bạn nên nén code lại thành file `.zip` để truyền tải nhanh hơn.
**Lưu ý**: Hãy loại bỏ các thư mục sau trước khi nén:
- `.git/` (Lịch sử git rất nặng)
- `target/` hoặc `build/` (File máy cá nhân build không chạy được trên server)
- `.idea/` hoặc `.vscode/` (Cấu hình IDE)

### 1.2 Lệnh nén bằng PowerShell (Window)
```powershell
# Nén toàn bộ thư mục trừ các folder rác
Compress-Archive -Path .\* -DestinationPath microservice.zip
```

---

## 2. Gửi file lên Server bằng SCP

Sử dụng lệnh `scp` (Secure Copy) để đẩy file qua tunnel SSH.

### Lệnh PowerShell mẫu:
```powershell
# scp -i <đường-dẫn-private-key> <file-nguồn> <user>@<ip-server>:<thư-mục-đích>
scp -i ~/.ssh/id_rsa microservice.zip root@123.456.78.9:/app/
```

---

## 3. Thao tác trên Server (Remote SSH)

Truy cập vào server để giải nén và thực hiện build.

### 3.1 Kết nối SSH
```bash
ssh -i ~/.ssh/id_rsa root@123.456.78.9
```

### 3.2 Giải nén và Dọn dẹp
```bash
cd /app
unzip microservice.zip -d microservice_new
rm microservice.zip
```

---

## 4. Quản lý Biến môi trường (Senior Pattern)

Khi chạy thủ công, bạn cần tự tạo file `.env` để chứa các thông tin nhạy cảm.

### Tạo file .env tại thư mục gốc của dự án trên server:
```bash
nano .env
```
**Nội dung mẫu**:
```env
POSTGRES_USER=myuser
POSTGRES_PASSWORD=mypassword
KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:29092
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
```

---

## 5. Chạy Docker Compose trên Server

Sau khi đã có code và file `.env`, bạn thực hiện build và chạy:

```bash
cd /app/microservice_new

# Build lại toàn bộ image từ source code mới nhất
docker-compose --env-file .env up -d --build

# Kiểm tra trạng thái các container
docker-compose ps
```

---

## 6. Mẹo Senior: Dùng RSYNC (Nếu dùng Linux/WSL)

Nếu bạn không muốn nén file, hãy dùng `rsync`. Nó chỉ gửi các file có thay đổi (delta), cực kỳ nhanh.

```bash
rsync -avz --exclude 'target' --exclude '.git' ./ root@123.456.78.9:/app/microservice/
```

---
> [!WARNING]
> **Cảnh báo**: Triển khai thủ công rất dễ gây ra lỗi "môi trường không đồng nhất" (máy mình chạy được nhưng server thì không). Do đó, hãy ưu tiên đầu tư vào CI/CD tự động sớm nhất có thể!
