# Kế hoạch Triển khai CI/CD (Senior DevOps Architecture)

Chào bạn, đây là bản kế hoạch chi tiết để đưa hệ thống Microservices của chúng ta lên quy trình tự động hóa hoàn toàn (CI/CD) sử dụng **GitHub Actions**. Tôi sẽ giải thích tỉ mỉ từng giai đoạn và ý nghĩa của các lệnh để bạn nắm bắt được tư duy vận hành (Operations).

---

## 1. Điều kiện cần để triển khai CI/CD (Prerequisites)

Để hệ thống này vận hành trơn tru, một "Senior" sẽ luôn chuẩn bị kỹ các yếu tố sau:

### 1.1 Hạ tầng Mã nguồn & Registry
- **GitHub Repository**: Nơi lưu trữ code của toàn bộ microservices.
- **Docker Hub / GitHub Packages**: Registry để lưu trữ các "khuôn mẫu" (Images) sau khi build. Bạn cần tạo account và lấy **Access Token**.

### 1.2 Hạ tầng Server (Target Server)
- **Linux Server (Ubuntu/CentOS)**: Khuyên dùng VPS/Cloud có ít nhất 4GB RAM trở lên.
- **Docker & Docker Compose**: Đã được cài đặt sẵn trên Server.
- **SSH Access**: Server phải mở port 22 và cho phép truy cập bằng Private Key (tránh dùng mật khẩu để bảo mật).

### 1.3 Quản lý Tài liệu & Biến môi trường (Secrets)
- Các file cấu hình nhạy cảm (`.env`, `application-prod.yml`) **TUYỆT ĐỐI** không push lên GitHub. Chúng ta sẽ cấu hình chúng trực tiếp trên Server hoặc sử dụng **GitHub Secrets**.

### 1.4 Lựa chọn hạ tầng: VM (Máy ảo local) hay VPS (Cloud)?
Đây là thắc mắc rất phổ biến. Theo kinh nghiệm của tôi:
- **Dùng VM (Máy ảo của bạn)**: Rất tốt để **Học và Test**. Bạn không tốn phí, có toàn quyền kiểm soát. 
  - *Vấn đề*: GitHub Actions (Cloud) không thể SSH vào máy ảo của bạn vì nó nằm sau router (NAT).
  - *Giải pháp Senior*: Sử dụng **GitHub Self-hosted Runner**. Bạn cài một phần mềm nhỏ của GitHub lên máy ảo, nó sẽ chủ động kết nối lên GitHub để nhận lệnh build/deploy. Đây là cách tốt nhất để triển khai CI/CD trên máy cá nhân mà không cần IP Public.
- **Dùng VPS (Cloud)**: Cần thiết cho **Dự án thực tế**. Hệ thống luôn online, GitHub Actions kết nối trực tiếp qua SSH một cách dễ dàng.

---

## 2. Tổng Quan Mô Hình CI/CD

Chúng ta sẽ xây dựng một Pipeline (đường ống) gồm 3 giai đoạn chính:

1.  **Continuous Integration (CI)**: Tự động Kiểm tra & Build code.
2.  **Containerization**: Tự động đóng gói Docker Image & Đẩy lên Registry.
3.  **Continuous Deployment (CD)**: Tự động cập nhật hệ thống trên Server.

```mermaid
graph LR
    Dev[Developer] -->|Push Code| GH[GitHub Repo]
    GH -->|Trigger| Action[GitHub Actions]
    Action --> CI[CI: Test & Build]
    CI --> Img[Build Docker Image]
    Img --> Push[Push to Docker Hub]
    Push --> Deploy[Deploy to Server]
```

---

## 2. Chi Tiết Pipeline & Giải Thích Lệnh

Dưới đây là mô phỏng cấu trúc file `.github/workflows/main.yml` và giải thích từng phần:

### Giai đoạn 1: Khởi tạo (Setting up)
```yaml
name: Microservices CI/CD Pipeline
on:
  push:
    branches: [ main ] # Kích hoạt pipeline khi code được push vào nhánh main
```
*   **Giải thích**: Rule này giúp bảo vệ môi trường Production. Chỉ khi code đã qua review và merge vào `main` thì quy trình deploy mới bắt đầu.

### Giai đoạn 2: CI - Java Build & Test
```yaml
jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4 # 1. Lấy source code từ repo về môi trường build
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4 # 2. Cài đặt Java 21 LTS
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven' # 3. Cấu hình Cache Maven - Cực kỳ quan trọng để build nhanh hơn
          
      - name: Build with Maven
        run: mvn clean package -DskipTests # 4. Build thử nghiệm xem code có lỗi compile không
```
*   **Senior Note**: Việc dùng `cache: 'maven'` sẽ giúp GitHub lưu lại các thư viện `.m2`. Lần chạy sau sẽ không phải tải lại toàn bộ, tiết kiệm 50-70% thời gian build.

### Giai đoạn 3: Docker Build & Push
```yaml
  docker-push:
    needs: build-and-test # Chỉ chạy khi bước CI thành công
    steps:
      - name: Login to Docker Hub
        uses: docker/login-action@v3 # 5. Đăng nhập vào Docker Hub bằng Secrets
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}
          
      - name: Build and Push Order Service
        uses: docker/build-push-action@v5 # 6. Tự động Build & Push image
        with:
          context: .
          file: order-service/Dockerfile
          push: true
          tags: user/order-service:latest
```
*   **Giải thích**: `secrets.DOCKERHUB_TOKEN` là biến môi trường bảo mật. Chúng ta không bao giờ hardcode mật khẩu vào file yaml.

### Giai đoạn 4: CD - Deployment (Triển khai)
```yaml
  deploy:
    needs: docker-push
    runs-on: ubuntu-latest
    steps:
      - name: Deploy via SSH
        uses: appleboy/ssh-action@master # 7. Kết nối SSH vào Server của bạn
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd /app/microservice
            docker-compose pull # 8. Tải image mới nhất về
            docker-compose up -d # 9. Cập nhật container mà không làm gián đoạn hệ thống
```
*   **Giải thích**: Lệnh `docker-compose pull` sẽ kéo image `:latest` mà chúng ta vừa push ở bước 3, sau đó `up -d` sẽ restart container với phiên bản mới nhất.

---

## 3. Các bước bạn cần chuẩn bị (Cấu hình một lần)

### 3.1 Lấy các giá trị này ở đâu? (Sources)

Để điền vào GitHub Secrets, bạn cần chuẩn bị các thông tin sau:

| Biến (Secret Name) | Lấy từ đâu? | Cách lấy |
| :--- | :--- | :--- |
| **DOCKERHUB_USERNAME** | Account Docker Hub | Chính là tên đăng nhập hoặc email bạn dùng cho `hub.docker.com`. |
| **DOCKERHUB_TOKEN** | Docker Hub Settings | `Account Settings` -> `Security` -> `Personal Access Tokens` -> `Generate new token`. |
| **SERVER_HOST** | Nhà cung cấp VPS | Địa chỉ IP Public của Server (ví dụ: `123.45.67.89`). |
| **SERVER_USER** | Root hoặc User riêng | Thường là `root`, `ubuntu`, hoặc `centos`. |
| **SSH_PRIVATE_KEY** | Máy cá nhân của bạn | Chạy lệnh `ssh-keygen` để tạo cặp khóa. Lấy nội dung file `id_rsa` (khóa bí mật). |

### 3.2 Cách cấu hình trên GitHub (How to set)

1. Truy cập Repo trên GitHub -> **Settings**.
2. Tìm mục **Secrets and variables** -> **Actions**.
3. Nhấn **New repository secret**.
4. Nhập đúng **Name** (ví dụ: `DOCKERHUB_TOKEN`) và dán giá trị vào ô **Value**.

---

## 4. Quản lý Biến môi trường (Environment Variables)

Khác với Secrets (thông tin nhạy cảm), các biến cấu hình thông thường được lấy từ 2 nguồn:

### 4.1 Biến trong file Workflow (.yml)
Dùng cho các thông tin cố định như tên image, port, hoặc profile:
```yaml
env:
  SPRING_PROFILES_ACTIVE: prod
  SERVICE_NAME: order-service
```

### 4.2 Biến trong file .env (Trên Server)
Dùng cho các thông tin thay đổi theo môi trường (Staging/Prod). File này sẽ nằm sẵn trên Server trong thư mục dự án:
- `docker-compose.yml` sẽ đọc file này thông qua lệnh `env_file: .env` hoặc tự động nhận diện nếu cùng thư mục.
- **Lợi ích**: Bạn có thể sửa cấu hình database trực tiếp trên server mà không cần sửa code hay chạy lại CI/CD.

---

## 5. Tại sao phải viết cấu hình vào file .yml? (Tư duy Pipeline as Code)

Đây là một câu hỏi rất hay. Trong giới DevOps chuyên nghiệp, chúng ta gọi đây là mô hình **Pipeline as Code**. Dưới đây là 5 lý do cốt lõi tại sao một Senior sẽ không bao giờ cấu hình CI/CD bằng giao diện kéo thả (UI) mà luôn dùng YAML:

### 5.1 Quy định bắt buộc của GitHub
GitHub Actions được thiết kế để tự động hóa dựa trên sự kiện (Event-driven). Nó chỉ quét duy nhất thư mục `.github/workflows/` để tìm các file `.yml`. Nếu bạn không có file này, GitHub sẽ không biết phải build code của bạn ở đâu và như thế nào.

### 5.2 Quản trị bằng Version Control (Git)
Khi cấu hình nằm trong file `.yml`, nó trở thành một phần của mã nguồn:
- **Lịch sử thay đổi**: Bạn biết chính xác ai đã sửa lệnh build, sửa cái gì và tại sao.
- **Khả năng Rollback**: Nếu bản deploy hôm nay bị lỗi do cấu hình sai, bạn chỉ cần `git revert` lại file `.yml` của ngày hôm qua. Hệ thống sẽ ngay lập tức quay về trạng thái ổn định.

### 5.3 YAML: Ngôn ngữ trung gian hoàn hảo
YAML (Yet Another Markup Language) không phải là mã thực thi mà là **mã cấu hình**.
- **Với con người**: Nó cực kỳ dễ đọc (Human-readable) nhờ cấu trúc phân cấp bằng khoảng trắng.
- **Với máy móc**: Các con Bot build (Runners) có thể đọc và xẻ nhỏ dữ liệu này cực nhanh để thực thi đúng thứ tự.

### 5.4 Tính Nhất quán và Di động (Portability)
Hãy tưởng tượng bạn có 15 microservices. Nếu cấu hình bằng tay trên giao diện web, bạn sẽ phải lặp lại hàng trăm cú click chuột. Với YAML:
- Bạn chỉ cần viết 1 file chuẩn, sau đó **Copy & Paste** sang các service khác.
- Nếu bạn chuyển sang một Repo mới, chỉ cần mang theo thư mục `.github` là toàn bộ thực thể CI/CD sẽ "sống lại" ngay lập tức mà không cần cấu hình lại từ đầu.

### 5.5 Cách ly lỗi (Isolation) trong Microservices
Trong dự án của chúng ta, mỗi service có một file `.yml` riêng:
- Nếu bạn sửa lỗi cho `identity-service`, chỉ file `identity-service.yml` chạy. 
- Điều này giúp hệ thống vận hành cực kỳ an toàn, không bao giờ có chuyện sửa service này làm hỏng pipeline của service kia.

> [!IMPORTANT]
> **Senior Insight**: Việc dùng file `.yml` giúp xóa bỏ khái niệm "Máy tôi chạy được nhưng máy build thì không". Vì mọi lệnh build đều được định nghĩa rõ ràng, minh bạch và ai cũng có thể đọc được ngay trong Repo.

---

## 6. Tại sao đây là mẫu thiết kế "Senior"?

1.  **Fail-Fast**: Nếu bước `build-and-test` lỗi, pipeline sẽ dừng ngay lập tức, không cho phép đẩy code lỗi lên production.
2.  **Idempotency**: Quy trình deploy được thiết kế để dù chạy lại nhiều lần cũng không gây lỗi hệ thống.
3.  **Security**: Toàn bộ thông tin nhạy cảm đều được mã hóa bằng **GitHub Secrets**.
4.  **Traceability**: Mỗi bản deploy đều gắn với một commit ID, giúp bạn biết chính xác ai đã deploy cái gì và khi nào.

> [!TIP]
> **Lời khuyên**: Hãy bắt đầu với việc tự động hóa (CI) cho code trước, sau đó mới đến tự động hóa đóng gói (Docker), và cuối cùng mới là tự động deploy. Đừng làm tất cả cùng lúc để dễ debug!

Bạn thấy kế hoạch này thế nào? Nếu bạn đồng ý, tôi sẽ tạo thư mục cấu hình `.github/workflows` và bắt đầu viết file CI mẫu cho service đầu tiên.
