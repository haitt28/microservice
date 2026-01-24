'use client';

import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
    User, Package, MapPin, Settings, LogOut,
    CreditCard, Bell, ChevronRight, Clock, CheckCircle2
} from 'lucide-react';

const TABS = [
    { id: 'overview', label: 'Tổng quan', icon: User },
    { id: 'orders', label: 'Đơn hàng', icon: Package },
    { id: 'addresses', label: 'Địa chỉ', icon: MapPin },
    { id: 'settings', label: 'Cài đặt', icon: Settings },
];

export default function ProfilePage() {
    const [activeTab, setActiveTab] = useState('overview');

    return (
        <div className="section-container py-12">
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-12">
                {/* Sidebar Nav */}
                <aside className="space-y-8">
                    <div className="glass-card p-6 flex items-center gap-4">
                        <div className="w-16 h-16 rounded-full bg-gradient-to-br from-accent to-amber-200 flex items-center justify-center text-white text-2xl font-black">
                            NA
                        </div>
                        <div>
                            <h2 className="font-bold text-slate-800">Nguyễn Văn An</h2>
                            <p className="text-xs text-slate-400 font-medium">Thành viên từ 2024</p>
                        </div>
                    </div>

                    <nav className="glass-card overflow-hidden">
                        {TABS.map((tab) => {
                            const Icon = tab.icon;
                            return (
                                <button
                                    key={tab.id}
                                    onClick={() => setActiveTab(tab.id)}
                                    className={`w-full flex items-center gap-4 px-6 py-4 text-sm font-bold transition-all border-l-4 ${activeTab === tab.id
                                            ? 'bg-slate-50 border-accent text-primary'
                                            : 'border-transparent text-slate-400 hover:text-slate-600 hover:bg-slate-50/50'
                                        }`}
                                >
                                    <Icon size={18} />
                                    {tab.label}
                                </button>
                            );
                        })}
                        <button className="w-full flex items-center gap-4 px-6 py-4 text-sm font-bold text-error hover:bg-red-50 transition-all border-l-4 border-transparent mt-4">
                            <LogOut size={18} />
                            Đăng xuất
                        </button>
                    </nav>
                </aside>

                {/* Dynamic Content */}
                <main className="lg:col-span-3">
                    <AnimatePresence mode="wait">
                        <motion.div
                            key={activeTab}
                            initial={{ opacity: 0, x: 20 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: -20 }}
                            transition={{ duration: 0.3 }}
                            className="space-y-8"
                        >
                            {activeTab === 'overview' && <OverviewTab />}
                            {activeTab === 'orders' && <OrdersTab />}
                            {activeTab === 'addresses' && <AddressesTab />}
                        </motion.div>
                    </AnimatePresence>
                </main>
            </div>
        </div>
    );
}

function OverviewTab() {
    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="glass-card p-8 space-y-4 border-b-4 border-blue-500">
                <div className="p-3 bg-blue-50 text-blue-500 rounded-2xl w-fit"><Package size={24} /></div>
                <div className="text-3xl font-black">12</div>
                <p className="text-slate-400 text-sm font-bold">Đơn hàng hoàn tất</p>
            </div>
            <div className="glass-card p-8 space-y-4 border-b-4 border-amber-500">
                <div className="p-3 bg-amber-50 text-warning rounded-2xl w-fit"><CreditCard size={24} /></div>
                <div className="text-3xl font-black">2.4M</div>
                <p className="text-slate-400 text-sm font-bold">Tổng chi tiêu (VND)</p>
            </div>
            <div className="glass-card p-8 space-y-4 border-b-4 border-emerald-500">
                <div className="p-3 bg-emerald-50 text-success rounded-2xl w-fit"><Bell size={24} /></div>
                <div className="text-3xl font-black">5</div>
                <p className="text-slate-400 text-sm font-bold">Thông báo mới</p>
            </div>
        </div>
    );
}

function OrdersTab() {
    return (
        <div className="space-y-6">
            <h3 className="text-2xl font-black tracking-tighter uppercase italic">Lịch sử đơn hàng</h3>
            {[1, 2].map((i) => (
                <div key={i} className="glass-card overflow-hidden">
                    <div className="bg-slate-50 px-6 py-4 flex justify-between items-center border-b border-slate-100">
                        <div className="flex gap-6 text-sm font-bold">
                            <div>
                                <p className="text-slate-400 text-[10px] uppercase">Mã đơn hàng</p>
                                <p className="text-primary mt-1">#ORD-8829-XJ{i}</p>
                            </div>
                            <div>
                                <p className="text-slate-400 text-[10px] uppercase">Ngày đặt</p>
                                <p className="text-slate-600 mt-1">20/01/2024</p>
                            </div>
                        </div>
                        <div className="px-3 py-1 bg-emerald-100 text-success text-[10px] font-black rounded-full uppercase">
                            Hoàn thành
                        </div>
                    </div>
                    <div className="p-6 flex items-center justify-between">
                        <div className="flex items-center gap-4">
                            <div className="w-16 h-16 bg-slate-100 rounded-xl"></div>
                            <div>
                                <h4 className="font-bold text-slate-800">Fiinx Pro headphones ...và 2 sản phẩm khác</h4>
                                <p className="text-sm text-slate-400">Tổng cộng: 4.500.000đ</p>
                            </div>
                        </div>
                        <button className="flex items-center gap-2 text-sm font-bold text-primary hover:text-accent transition-colors">
                            Chi tiết <ChevronRight size={18} />
                        </button>
                    </div>
                </div>
            ))}
        </div>
    );
}

function AddressesTab() {
    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h3 className="text-2xl font-black tracking-tighter uppercase italic">Sổ địa chỉ</h3>
                <button className="btn-primary py-2 px-4 text-xs">Thêm địa chỉ mới</button>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="glass-card p-6 border-2 border-accent relative ring-4 ring-accent/5">
                    <div className="absolute top-4 right-4 text-accent font-black text-[10px] uppercase tracking-widest flex items-center gap-1">
                        <CheckCircle2 size={12} /> Mặc định
                    </div>
                    <h4 className="font-bold text-lg mb-4">Nhà riêng</h4>
                    <p className="text-slate-600 text-sm leading-relaxed mb-6 font-medium">
                        123 Đường Lê Lợi, Phường Bến Thành, Quận 1, TP. Hồ Chí Minh
                    </p>
                    <div className="flex gap-4">
                        <button className="text-xs font-bold text-slate-400 hover:text-primary transition-colors">Chỉnh sửa</button>
                        <button className="text-xs font-bold text-slate-400 hover:text-error transition-colors">Xóa</button>
                    </div>
                </div>
                <div className="glass-card p-6 border-2 border-transparent hover:border-slate-200 transition-all">
                    <h4 className="font-bold text-lg mb-4">Văn phòng</h4>
                    <p className="text-slate-600 text-sm leading-relaxed mb-6 font-medium">
                        Tòa nhà Landmark 81, Quận Bình Thạnh, TP. Hồ Chí Minh
                    </p>
                    <div className="flex gap-4">
                        <button className="text-xs font-bold text-slate-400 hover:text-primary transition-colors">Chỉnh sửa</button>
                        <button className="text-xs font-bold text-slate-400 hover:text-error transition-colors">Xóa</button>
                    </div>
                </div>
            </div>
        </div>
    );
}
