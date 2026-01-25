import { withAuth } from "next-auth/middleware";
import { NextResponse } from "next/server";

/**
 * Senior Note: Next.js Middleware - Xử lý Authorization tập trung.
 * 
 * - Kiểm tra quyền truy cập cho các route bảo mật (e.g. /admin).
 * - Redirect người dùng không có quyền (RBAC) về trang unauthorized.
 * - Tận dụng NextAuth để trích xuất JWT/Token từ request.
 */

export default withAuth(
    function middleware(req) {
        const token = req.nextauth.token;
        const isAdminRoute = req.nextUrl.pathname.startsWith('/admin');

        // Kiểm tra nếu người dùng đang cố gắng truy cập các route quản trị
        if (isAdminRoute) {
            const roles = token?.roles as string[] || [];
            const isAdmin = roles.includes('admin');

            // Điều hướng đến trang không có quyền nếu người dùng không có role admin
            if (!isAdmin) {
                return NextResponse.redirect(new URL('/unauthorized', req.url));
            }
        }

        return NextResponse.next();
    },
    {
        callbacks: {
            authorized: ({ token, req }) => {
                const isAdminRoute = req.nextUrl.pathname.startsWith('/admin');

                // Các route quản trị yêu cầu phải được xác thực (Authenticated)
                if (isAdminRoute) {
                    return !!token;
                }

                // Tất cả các route khác đều có thể truy cập tự do
                return true;
            },
        },
        pages: {
            signIn: '/login',
        },
    }
);

// Bảo vệ các admin routes (Matcher pattern)
export const config = {
    matcher: ['/admin/:path*'],
};
