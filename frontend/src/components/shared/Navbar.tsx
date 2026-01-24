'use client';

import Link from 'next/link';
import { ShoppingBag, User, Search, Menu, LogOut, LayoutDashboard, X } from 'lucide-react';
import { useCartStore } from '@/store/cartStore';
import { useSession, signOut } from 'next-auth/react';
import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Input } from '@/components/ui';
import { usePathname } from 'next/navigation';

export default function Navbar() {
    const { data: session }: any = useSession();
    const pathname = usePathname();
    const totalItems = useCartStore((state) => state.totalItems());
    const [isAccountOpen, setIsAccountOpen] = useState(false);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [isScrolled, setIsScrolled] = useState(false);
    const [mounted, setMounted] = useState(false);
    const [isSearchOpen, setIsSearchOpen] = useState(false);

    // Prevent hydration mismatch
    useEffect(() => {
        setMounted(true);
    }, []);

    // Close menus on path change
    useEffect(() => {
        setIsAccountOpen(false);
        setIsMobileMenuOpen(false);
        setIsSearchOpen(false);
    }, [pathname]);

    // Handle scroll effect
    useEffect(() => {
        const handleScroll = () => setIsScrolled(window.scrollY > 20);
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const NAV_LINKS = [
        { label: 'Sản phẩm', href: '/products' },
        { label: 'Danh mục', href: '/categories' },
        { label: 'Khuyến mãi', href: '/deals', className: 'text-amber-500' },
    ];

    return (
        <>
            <nav
                className={`fixed top-0 left-0 right-0 z-[100] transition-all duration-500 ${isScrolled
                    ? 'py-3 backdrop-blur-xl bg-white/90 border-b border-slate-100 shadow-sm'
                    : 'py-6 bg-transparent'
                    }`}
            >
                <div className="section-container flex items-center justify-between">
                    <div className="flex items-center gap-12">
                        <Link href="/" className="text-2xl font-black tracking-tighter text-slate-900 hover:scale-105 transition-transform">
                            LUXORA<span className="text-amber-400">.</span>
                        </Link>

                        <div className="hidden md:flex items-center gap-8">
                            {NAV_LINKS.map(link => (
                                <Link
                                    key={link.href}
                                    href={link.href}
                                    className={`text-sm font-bold tracking-wide transition-all hover:text-amber-500 uppercase ${pathname === link.href ? 'text-amber-500' : 'text-slate-600'
                                        } ${link.className || ''}`}
                                >
                                    {link.label}
                                </Link>
                            ))}
                        </div>
                    </div>

                    <div className="flex items-center gap-2 sm:gap-4">
                        {/* Search Toggle */}
                        <div className="relative flex items-center">
                            <AnimatePresence>
                                {isSearchOpen && (
                                    <motion.div
                                        initial={{ width: 0, opacity: 0 }}
                                        animate={{ width: 250, opacity: 1 }}
                                        exit={{ width: 0, opacity: 0 }}
                                        transition={{ duration: 0.3 }}
                                        className="overflow-hidden mr-2"
                                    >
                                        <Input
                                            type="search"
                                            placeholder="Tìm kiếm..."
                                            className="!py-2 !rounded-full"
                                            autoFocus
                                            onBlur={() => setTimeout(() => setIsSearchOpen(false), 200)}
                                        />
                                    </motion.div>
                                )}
                            </AnimatePresence>
                            <button
                                onClick={() => setIsSearchOpen(!isSearchOpen)}
                                className="p-3 text-slate-600 hover:bg-slate-100 rounded-full transition-all max-sm:hidden"
                            >
                                {isSearchOpen ? <X size={20} /> : <Search size={20} />}
                            </button>
                        </div>

                        <Link href="/cart" className="relative p-3 text-slate-600 hover:bg-slate-100 rounded-full transition-all">
                            <ShoppingBag size={20} />
                            {totalItems > 0 && (
                                <span className="absolute top-2 right-2 bg-slate-900 text-white text-[10px] font-black w-4 h-4 flex items-center justify-center rounded-full ring-2 ring-white animate-in zoom-in">
                                    {totalItems}
                                </span>
                            )}
                        </Link>

                        {mounted && session ? (
                            <div className="relative">
                                <button
                                    onClick={() => setIsAccountOpen(!isAccountOpen)}
                                    className="flex items-center gap-2 bg-white/50 backdrop-blur-md hover:bg-white border border-slate-100 px-4 py-2 rounded-[20px] transition-all shadow-sm"
                                >
                                    <div className="w-6 h-6 rounded-full bg-slate-900 text-white text-[8px] flex items-center justify-center font-black uppercase">
                                        {session.user?.name?.substring(0, 2) || 'US'}
                                    </div>
                                    <span className="sm:inline w-0 sm:w-auto overflow-hidden sm:overflow-visible text-xs font-bold text-slate-800 tracking-tight">{session.user?.name?.split(' ')[0]}</span>
                                </button>

                                <AnimatePresence>
                                    {isAccountOpen && (
                                        <motion.div
                                            initial={{ opacity: 0, y: 10, scale: 0.95 }}
                                            animate={{ opacity: 1, y: 0, scale: 1 }}
                                            exit={{ opacity: 0, y: 10, scale: 0.95 }}
                                            className="absolute right-0 mt-3 w-64 bg-white border border-slate-100 rounded-[28px] p-2 shadow-2xl z-[110] overflow-hidden"
                                        >
                                            <div className="px-6 py-5 bg-slate-50 border-b border-slate-100 mb-2">
                                                <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Tài khoản</p>
                                                <p className="text-sm font-bold text-slate-900 truncate">{session.user?.name}</p>
                                            </div>
                                            <Link href="/profile" className="flex items-center gap-3 px-4 py-4 text-xs font-bold text-slate-600 hover:bg-slate-50 hover:text-slate-900 rounded-2xl transition-all">
                                                <User size={16} /> Trang cá nhân
                                            </Link>
                                            <Link href="/admin" className="flex items-center gap-3 px-4 py-4 text-xs font-bold text-slate-600 hover:bg-slate-50 hover:text-slate-900 rounded-2xl transition-all">
                                                <LayoutDashboard size={16} /> Quản trị (Admin)
                                            </Link>
                                            <div className="h-px bg-slate-100 my-1 mx-4" />
                                            <button
                                                onClick={() => signOut()}
                                                className="w-full flex items-center gap-3 px-4 py-4 text-xs font-bold text-rose-500 hover:bg-rose-50 rounded-2xl transition-all"
                                            >
                                                <LogOut size={16} /> Đăng xuất
                                            </button>
                                        </motion.div>
                                    )}
                                </AnimatePresence>
                            </div>
                        ) : mounted && !session ? (
                            <Link href="/login" className="flex items-center gap-2 bg-slate-900 text-white px-6 sm:px-8 py-3 rounded-[20px] text-xs font-black uppercase tracking-widest hover:bg-slate-800 hover:scale-105 transition-all shadow-xl shadow-slate-200">
                                <User size={16} />
                                <span className="sm:inline w-0 sm:w-auto overflow-hidden sm:overflow-visible">Đăng nhập</span>
                            </Link>
                        ) : (
                            <div className="w-24 h-10 bg-slate-200 rounded-[20px] animate-pulse" />
                        )}

                        <button
                            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                            className="md:hidden p-3 text-slate-600 hover:bg-slate-100 rounded-full transition-all"
                        >
                            <Menu size={20} />
                        </button>
                    </div>
                </div>
            </nav>

            {/* SPACER: Optimized to ensure no content is hidden by fixed header */}
            <div className="h-28 md:h-32 pointer-events-none" />

            {/* Mobile Menu Overlay */}
            <AnimatePresence>
                {isMobileMenuOpen && (
                    <motion.div
                        initial={{ opacity: 0, x: '100%' }}
                        animate={{ opacity: 1, x: 0 }}
                        exit={{ opacity: 0, x: '100%' }}
                        className="fixed inset-0 z-[200] bg-white p-6 md:hidden flex flex-col"
                    >
                        <div className="flex justify-between items-center mb-12">
                            <span className="text-2xl font-black tracking-tighter">LUXORA.</span>
                            <button onClick={() => setIsMobileMenuOpen(false)} className="p-3 bg-slate-100 rounded-full">
                                <X size={24} />
                            </button>
                        </div>

                        <div className="flex flex-col gap-8">
                            {NAV_LINKS.map(link => (
                                <Link
                                    key={link.href}
                                    href={link.href}
                                    className="text-5xl font-black tracking-tighter text-slate-900 uppercase italic leading-none hover:text-amber-400 transition-colors"
                                >
                                    {link.label}
                                </Link>
                            ))}
                        </div>

                        <div className="mt-auto space-y-6 pt-12 border-t border-slate-100">
                            <Link href="/profile" className="flex items-center gap-4 text-xl font-black uppercase tracking-widest text-slate-500 hover:text-slate-900">
                                <User size={28} /> Tài khoản
                            </Link>
                            <button onClick={() => signOut()} className="flex items-center gap-4 text-xl font-black uppercase tracking-widest text-rose-500 hover:bg-rose-50 p-4 -ml-4 rounded-3xl w-full">
                                <LogOut size={28} /> Đăng xuất
                            </button>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </>
    );
}
