import { withAuth } from "next-auth/middleware";
import { NextResponse } from "next/server";

export default withAuth(
    function middleware(req) {
        const token = req.nextauth.token;
        const isAdminRoute = req.nextUrl.pathname.startsWith('/admin');

        // Check if user is trying to access admin routes
        if (isAdminRoute) {
            const roles = token?.roles as string[] || [];
            const isAdmin = roles.includes('admin');

            // Redirect to unauthorized page if user doesn't have admin role
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

                // Admin routes require authentication
                if (isAdminRoute) {
                    return !!token;
                }

                // All other routes are accessible
                return true;
            },
        },
        pages: {
            signIn: '/login',
        },
    }
);

// Protect admin routes
export const config = {
    matcher: ['/admin/:path*'],
};
