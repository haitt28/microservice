# Hướng dẫn cấu hình Keycloak cho LUXORA

## Tổng quan
Tài liệu này hướng dẫn cấu hình Keycloak để tích hợp SSO và phân quyền admin cho hệ thống LUXORA.

## Bước 1: Truy cập Keycloak Admin Console

1. Mở trình duyệt và truy cập: `http://localhost:8080`
2. Đăng nhập với tài khoản admin của Keycloak

## Bước 2: Tạo hoặc chọn Realm

1. Click vào dropdown realm (góc trên bên trái)
2. Click **"Create Realm"** hoặc chọn realm hiện có
3. Đặt tên: `luxora-realm`
4. Click **"Create"**

## Bước 3: Tạo Client cho Frontend

1. Trong realm `luxora-realm`, vào **Clients** → **Create client**
2. Cấu hình như sau:
   - **Client ID**: `luxora-frontend`
   - **Client Protocol**: `openid-connect`
   - Click **Next**
3. Client authentication settings:
   - **Client authentication**: ON (để lấy client secret)
   - **Authorization**: OFF
   - **Authentication flow**: 
     - ✅ Standard flow
     - ✅ Direct access grants
   - Click **Next**
4. Login settings:
   - **Valid Redirect URIs**: 
     - `http://localhost:3000/*`
     - `http://localhost:3001/*` (nếu dùng port 3001)
   - **Valid post logout redirect URIs**: `http://localhost:3000/*`
   - **Web Origins**: `http://localhost:3000`
   - Click **Save**

## Bước 4: Lấy Client Secret

1. Vào **Clients** → chọn `luxora-frontend`
2. Vào tab **Credentials**
3. Copy **Client Secret**
4. Paste vào file `.env.local`:
   ```
   KEYCLOAK_CLIENT_SECRET=<paste-secret-here>
   ```

## Bước 5: Tạo Role "admin"

1. Vào **Realm roles** → **Create role**
2. **Role name**: `admin`
3. **Description**: `Administrator role for LUXORA system`
4. Click **Save**

## Bước 6: Tạo User và gán Role

### Tạo user mới:
1. Vào **Users** → **Add user**
2. Điền thông tin:
   - **Username**: `admin@luxora.vn`
   - **Email**: `admin@luxora.vn`
   - **First name**: `Admin`
   - **Last name**: `User`
   - **Email verified**: ON
3. Click **Create**

### Đặt password:
1. Vào tab **Credentials**
2. Click **Set password**
3. Nhập password (ví dụ: `admin123`)
4. **Temporary**: OFF (để không phải đổi password lần đầu)
5. Click **Save**

### Gán role admin:
1. Vào tab **Role mapping**
2. Click **Assign role**
3. Chọn **Filter by realm roles**
4. Tích chọn role `admin`
5. Click **Assign**

## Bước 7: Cấu hình Client Scopes (Quan trọng!)

Đảm bảo roles được include trong token:

1. Vào **Client scopes** → chọn `roles`
2. Vào tab **Mappers**
3. Tìm mapper tên `realm roles` hoặc tạo mới:
   - **Name**: `realm roles`
   - **Mapper Type**: `User Realm Role`
   - **Token Claim Name**: `realm_access.roles`
   - **Claim JSON Type**: `String`
   - **Add to ID token**: ON
   - **Add to access token**: ON
   - **Add to userinfo**: ON

## Bước 8: Cập nhật .env.local

Đảm bảo file `.env.local` có đầy đủ thông tin:

```bash
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=fiinx_secret_key_123456789

KEYCLOAK_CLIENT_ID=luxora-frontend
KEYCLOAK_CLIENT_SECRET=<your-actual-secret-from-step-4>
KEYCLOAK_ISSUER=http://localhost:8080/realms/luxora-realm

NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

## Bước 9: Test Authentication

1. Khởi động frontend: `npm run dev`
2. Truy cập: `http://localhost:3000/login`
3. Click **"Đăng nhập với Keycloak"**
4. Đăng nhập với user đã tạo ở Bước 6
5. Sau khi đăng nhập thành công, thử truy cập: `http://localhost:3000/admin`
6. Nếu user có role `admin`, sẽ vào được admin dashboard
7. Nếu không có role `admin`, sẽ bị redirect đến `/unauthorized`

## Troubleshooting

### Lỗi: "Invalid redirect_uri"
- Kiểm tra lại **Valid Redirect URIs** trong client settings
- Đảm bảo có thêm `/*` ở cuối URL

### Lỗi: "Invalid client credentials"
- Kiểm tra lại `KEYCLOAK_CLIENT_SECRET` trong `.env.local`
- Đảm bảo **Client authentication** đang ON

### Không thấy roles trong token
- Kiểm tra Client Scopes mapper (Bước 7)
- Đảm bảo user đã được gán role `admin`

### Vẫn bị redirect đến /unauthorized dù có role admin
- Mở DevTools → Application → Cookies → Xóa cookies
- Đăng xuất và đăng nhập lại
- Kiểm tra console log để xem roles có trong session không

## Kiểm tra Roles trong Token

Để debug, bạn có thể thêm code này vào `src/app/admin/layout.tsx`:

```typescript
useEffect(() => {
    console.log('Session:', session);
    console.log('User roles:', session?.user?.roles);
}, [session]);
```

Roles phải là array chứa `"admin"` để có quyền truy cập.
