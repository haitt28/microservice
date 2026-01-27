# Hướng dẫn Thiết lập GitHub Self-hosted Runner trên Máy ảo (VM)

Chào bạn, đây là giải pháp "Senior" để triển khai CI/CD ngay trên máy ảo local mà không cần tốn tiền thuê VPS hay cấu hình Port Forwarding phức tạp.

---

## 1. Tại sao dùng Self-hosted Runner?
- **Bảo mật**: Runner tự kết nối ra GitHub (Outbound), bạn không cần mở bất kỳ port nào trên Router.
- **Tốc độ**: Tận dụng tối đa RAM/CPU của máy thật bạn đang có. 
- **Tiết kiệm**: Hoàn toàn miễn phí.

---

## 2. Các bước thiết lập trên GitHub

1. Truy cập vào Repository của bạn trên GitHub.
2. Chọn **Settings** -> **Actions** -> **Runners**.
3. Nhấn nút **New self-hosted runner**.
4. Chọn hệ điều hành của Máy ảo (thường là **Linux**) và kiến trúc (thường là **X64**).

---

## 3. Các lệnh thực hiện trên Máy ảo (VM)

Bạn hãy copy các lệnh GitHub cung cấp và chạy trên terminal của Máy ảo. Dưới đây là mô phỏng quá trình:

### 3.1 Tải và giải nén Runner
```bash
# Tạo thư mục cho runner
mkdir actions-runner && cd actions-runner

# Tải gói cài đặt (Lưu ý: Link này sẽ thay đổi tùy phiên bản, lấy link từ GitHub của bạn)
curl -o actions-runner-linux-x64.tar.gz -L https://github.com/actions/runner/releases/download/...

# Giải nén
tar xzf ./actions-runner-linux-x64.tar.gz
```

### 3.2 Cấu hình kết nối (Cần Token từ GitHub)
Khi chạy lệnh này, GitHub sẽ hỏi tên Runner và các nhãn (labels).
```bash
./config.sh --url https://github.com/YOUR_USER/YOUR_REPO --token YOUR_TOKEN
```
**Senior Tip**: Khi GitHub hỏi "Enter the name of the runner group", hãy để mặc định. Khi hỏi "Enter the name of runner", hãy đặt tên dễ nhớ như `local-vm-runner`.

### 3.3 Cài đặt Runner như một Service (Chế độ chạy ngầm)
Đừng chỉ chạy `./run.sh` vì khi bạn tắt terminal nó sẽ dừng. Hãy cài nó như một dịch vụ của hệ thống:
```bash
sudo ./svc.sh install
sudo ./svc.sh start
```

---

## 4. Cách sử dụng trong Workflow CI/CD

Để GitHub Actions biết phải gửi lệnh xuống máy ảo của bạn thay vì dùng máy ảo của GitHub, bạn chỉ cần sửa một dòng trong file `.yml`:

```diff
jobs:
  build-and-deploy:
-   runs-on: ubuntu-latest
+   runs-on: self-hosted
```

---

## 5. Những "Cạm bẫy" cần lưu ý (Senior Warnings)

1. **Docker Permission**: Runner cần quyền chạy các lệnh Docker. Bạn phải thêm user chạy runner vào group docker:
   ```bash
   sudo usermod -aG docker $USER
   # Sau đó log out và log in lại máy ảo
   ```
2. **Dọn dẹp Disk**: Self-hosted runner không tự xóa image sau khi build. Bạn nên định kỳ chạy `docker system prune -f` trên máy ảo để tránh đầy ổ cứng.
3. **Môi trường**: Đảm bảo máy ảo đã cài sẵn **Docker**, **Docker Compose**, và **Java 21** (nếu bạn muốn build trực tiếp trên máy ảo mà không dùng container builder).

---
Bạn hãy thực hiện các bước trên trang GitHub của bạn trước để lấy **Token**. Nếu có bước nào bị lỗi (đặc biệt là lỗi phân quyền Docker), hãy báo tôi nhé!
