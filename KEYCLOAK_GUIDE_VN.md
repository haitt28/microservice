# Hướng Dẫn & Review Hệ Thống Định Danh Keycloak (Senior Level)

Chào bạn, dựa trên code `identity-service` hiện tại, tôi sẽ review chi tiết về Keycloak và giải đáp các thắc mắc của bạn dưới góc nhìn của một Senior Architect.

---

## 1. Review Implementation Hiện Tại & Gợi Ý Nâng Cấp

Hiện tại, `identity-service` của bạn chỉ có duy nhất API `/register`. Đây là mức **CƠ BẢN**. Để đạt chuẩn **Senior/Production**, bạn nên bổ sung các API sau:

### Các API Cần Thiết Phải Có:
1.  **Login (Auth API)**: Hiện tại app của bạn có thể đang dùng Keycloak Login Form mặc định. Nhưng nếu muốn làm App Mobile hoặc Custom UI, bạn cần API để trao đổi `username/password` lấy `Access Token`.
2.  **Logout (Revoke Token)**: Hủy bỏ session của user trên toàn hệ thống.
3.  **Refresh Token**: Khi Access Token hết hạn (thường sau 5-15 phút), client cần dùng Refresh Token để lấy key mới mà không bắt user đăng nhập lại.
4.  **Change Password / Forgot Password**: Gửi mail reset mật khẩu hoặc đổi pass khi đang login.
5.  **Profile Update**: Cho phép user đổi thông tin cá nhân (First Name, Last Name).

### Phân Quyền (RBAC - Role Based Access Control):
- Đừng chỉ dừng lại ở việc đăng ký user. Bạn nên bổ sung logic để gán **Default Roles** (ví dụ: role `USER`) ngay khi đăng ký.
- Một Senior sẽ thiết kế thêm API cho Admin để phân quyền (Grant Role) cho các user khác.

---

## 2. Giải Mã `realm-export.json` & Lưu Trữ Dữ Liệu

### File `realm-export.json` là gì?
- Đây là file **MẪU (Template)** chứa toàn bộ cấu hình của một "vương quốc" (Realm) trong Keycloak.
- Nó bao gồm: Clients (các app được phép kết nối), Roles, Groups, và thậm chí là User mẫu.
- **Có cần thiết không?** CỰC KỲ CẦN THIẾT khi deploy. Nó giúp bạn mang cấu hình từ máy này sang máy khác mà không phải click tay lại trên giao diện UI.

### Dữ liệu lưu ở đâu?
- **User thực tế KHÔNG lưu vào file `.json` này**.
- User được lưu vào **Database** của Keycloak (trong `docker-compose.yml` của bạn, Keycloak đang dùng chung instance **Postgres**).
- Mỗi khi bạn gọi API `register`, Keycloak sẽ ghi dữ liệu vào các bảng như `USER_ENTITY` trong Postgres.

---

## 3. Hướng Dẫn Các Chức Năng Chính Trên UI Keycloak

Khi bạn đăng nhập vào Admin Console (thường là `localhost:8080/admin`), hãy chú ý:

1.  **Realms (Góc trên cùng bên trái)**:
    - Giống như một "Tenant" hoặc "Phòng ban". Mỗi dự án lớn nên có 1 Realm riêng (e.g., `fiinx-realm`).
2.  **Clients**:
    - Là các ứng dụng muốn kết nối Keycloak. Bạn cần tạo Client cho `web-app`, `mobile-app`, và đặc biệt là `gateway` hoặc `microservices`.
3.  **Client Scopes**:
    - Định nghĩa các "quyền hạn" mà Client yêu cầu (e.g., quyền đọc email, quyền đọc profile).
4.  **Roles (Realm Roles & Client Roles)**:
    - Nơi bạn tạo `ADMIN`, `USER`, `MODERATOR`. Đây là dữ liệu dùng cho `@PreAuthorize` trong Spring Boot.
5.  **Users**:
    - Quản lý danh sách người dùng. Tại đây bạn có thể: Reset password cho user, Disable tài khoản, Gán role bằng tay.
6.  **Groups**:
    - Gom nhóm user để quản lý quyền theo phòng ban.

---

## 4. Senior Tip: Cơ Chế "Sync" User

Trong Microservices, chúng ta thường dùng mô hình **Hybrid User Storage**:
1.  **Keycloak**: Giữ thông tin bảo mật (Username, Password, Roles, Social Login).
2.  **Identity Service / Profile Service**: Giữ thông tin nghiệp vụ (Avatar, Địa chỉ, Số dư ví).

**Quy trình chuẩn:**
- API Register gọi Keycloak để tạo User.
- Keycloak tạo xong -> `identity-service` bắn 1 event qua **Kafka** (bạn đã có code này).
- Các service khác (như `profile-service`) lắng nghe Kafka và lưu User ID vào DB riêng của chúng để phục vụ query nhanh (Join dữ liệu).

> [!IMPORTANT]
> **Đừng lưu password trong DB của bạn**. Hãy để Keycloak làm việc đó tốt nhất. Nhiệm vụ của bạn là quản lý Access Token thật chặt chẽ.
