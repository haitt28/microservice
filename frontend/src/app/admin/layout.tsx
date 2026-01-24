'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useSession, signOut } from 'next-auth/react';
import { useEffect, useState } from 'react';
import {
    LayoutDashboard, Package, ShoppingCart, Users,
    Settings, BarChart3, Tag, Warehouse, BellRing, LogOut
} from 'lucide-react';

const MENU_ITEMS = [
    {
        group: 'Chính', items: [
            { label: 'Tổng quan', icon: LayoutDashboard, href: '/admin' },
            { label: 'Phân tích', icon: BarChart3, href: '/admin/analytics' },
        ]
    },
    {
        group: 'Kinh doanh', items: [
            { label: 'Sản phẩm', icon: Package, href: '/admin/products' },
            { label: 'Đơn hàng', icon: ShoppingCart, href: '/admin/orders' },
            { label: 'Kho hàng', icon: Warehouse, href: '/admin/inventory' },
            { label: 'Khuyến mãi', icon: Tag, href: '/admin/promotions' },
        ]
    },
    {
        group: 'Hệ thống', items: [
            { label: 'Người dùng', icon: Users, href: '/admin/users' },
            { label: 'Thông báo', icon: BellRing, href: '/admin/notifications' },
            { label: 'Cấu hình', icon: Settings, href: '/admin/settings' },
        ]
    },
];

export default function AdminLayout({ children }: { children: React.ReactNode }) {
    const pathname = usePathname();
    const { data: session, status } = useSession();
    const [mounted, setMounted] = useState(false);

    // Prevent hydration mismatch
    useEffect(() => {
        setMounted(true);
    }, []);

    const handleLogout = () => {
        if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
            signOut({ callbackUrl: '/' });
        }
    };

    // Get user initials for avatar
    const getUserInitials = () => {
        if (!session?.user?.name) return 'AD';
        return session.user.name
            .split(' ')
            .map(n => n[0])
            .join('')
            .toUpperCase()
            .slice(0, 2);
    };

    return (
        <div className="flex min-h-screen bg-slate-50">
            {/* Sidebar */}
            <aside className="fixed left-0 top-0 h-full w-64 bg-slate-900 border-r border-slate-800 text-slate-400 z-50">
                <div className="p-8 border-b border-slate-800">
                    <Link href="/" className="text-2xl font-black tracking-tighter text-white">
                        LUXORA<span className="text-accent">.</span>
                        <span className="text-[10px] bg-accent/20 text-accent px-2 py-0.5 rounded-full ml-2 align-middle">ADMIN</span>
                    </Link>
                </div>

                <nav className="p-6 space-y-8 h-[calc(100vh-100px)] overflow-y-auto scrollbar-hide">
                    {MENU_ITEMS.map((group) => (
                        <div key={group.group} className="space-y-3">
                            <h4 className="text-[10px] font-black uppercase tracking-widest text-slate-500 px-4">
                                {group.group}
                            </h4>
                            <div className="space-y-1">
                                {group.items.map((item) => {
                                    const Icon = item.icon;
                                    const isActive = pathname === item.href;
                                    return (
                                        <Link
                                            key={item.href}
                                            href={item.href}
                                            className={`flex items-center gap-3 px-4 py-3 text-sm font-bold rounded-xl transition-all ${isActive
                                                ? 'bg-accent text-white shadow-lg shadow-amber-500/20'
                                                : 'hover:bg-slate-800 hover:text-white'
                                                }`}
                                        >
                                            <Icon size={18} />
                                            {item.label}
                                        </Link>
                                    );
                                })}
                            </div>
                        </div>
                    ))}
                </nav>
            </aside>

            {/* Main Content */}
            <main className="ml-64 flex-1">
                {/* Admin Header */}
                <header className="h-20 bg-white border-b border-slate-200 px-8 flex items-center justify-between sticky top-0 z-40">
                    <h2 className="text-xl font-bold text-slate-800">Quản trị hệ thống</h2>
                    <div className="flex items-center gap-4">
                        {mounted && (
                            <>
                                <div className="text-right">
                                    <p className="text-sm font-bold text-slate-800">
                                        {session?.user?.name || 'Admin User'}
                                    </p>
                                    <p className="text-[10px] text-slate-500">
                                        {session?.user?.email || 'admin@luxora.vn'}
                                    </p>
                                </div>
                                <div className="w-10 h-10 rounded-full bg-gradient-to-br from-amber-400 to-orange-500 border-2 border-white shadow-lg flex items-center justify-center text-white font-bold text-sm">
                                    {getUserInitials()}
                                </div>
                                <button
                                    onClick={handleLogout}
                                    className="p-2 hover:bg-slate-100 rounded-xl transition-colors text-slate-600 hover:text-slate-900"
                                    title="Đăng xuất"
                                >
                                    <LogOut size={20} />
                                </button>
                            </>
                        )}
                        {!mounted && (
                            <div className="w-10 h-10 rounded-full bg-slate-200 animate-pulse" />
                        )}
                    </div>
                </header>

                <div className="p-8">
                    {children}
                </div>
            </main>
        </div>
    );
}
