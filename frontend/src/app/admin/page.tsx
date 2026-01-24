'use client';

import {
    BarChart3, Box, DollarSign,
    Package, ShoppingCart, Users,
    ArrowUpRight, ArrowDownRight, ArrowRight,
    Plus, Search, Filter, Download,
    TrendingUp, TrendingDown,
    ChevronRight, Calendar
} from 'lucide-react';
import { motion } from 'framer-motion';

const STATS = [
    { label: 'Doanh thu tháng này', value: '1.284.000.000đ', trend: '+12.5%', isUp: true, icon: DollarSign, color: 'emerald' },
    { label: 'Tổng đơn hàng', value: '3,456', trend: '+5.4%', isUp: true, icon: ShoppingCart, color: 'blue' },
    { label: 'Khách hàng mới', value: '+142', trend: '-2.1%', isUp: false, icon: Users, color: 'purple' },
    { label: 'Sản phẩm tồn kho', value: '856', trend: 'Ổn định', isUp: true, icon: Package, color: 'amber' },
];

const COLOR_MAP: Record<string, { bg: string; text: string }> = {
    emerald: { bg: 'bg-emerald-50', text: 'text-emerald-500' },
    blue: { bg: 'bg-blue-50', text: 'text-blue-500' },
    purple: { bg: 'bg-purple-50', text: 'text-purple-500' },
    amber: { bg: 'bg-amber-50', text: 'text-amber-500' },
};

export default function AdminDashboardPage() {
    return (
        <div className="space-y-8">
            {/* Page Header */}
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
                <div className="space-y-2">
                    <div className="flex items-center gap-2 text-xs font-semibold text-blue-600">
                        <Calendar size={14} /> Cập nhật lần cuối: 2 phút trước
                    </div>
                    <h1 className="text-3xl md:text-4xl font-bold text-slate-900">
                        Hệ thống quản trị
                    </h1>
                    <p className="text-sm text-slate-500">
                        Dashboard tổng quan • Phân tích thời gian thực
                    </p>
                </div>
                <div className="flex items-center gap-3">
                    <button className="flex items-center gap-2 px-5 py-3 bg-white border border-slate-200 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-50 transition-all shadow-sm">
                        <Download size={16} /> Báo cáo
                    </button>
                    <button className="flex items-center gap-2 px-5 py-3 bg-slate-900 text-white rounded-xl text-sm font-semibold hover:bg-slate-800 transition-all shadow-lg">
                        <Plus size={16} /> Sản phẩm mới
                    </button>
                </div>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {STATS.map((stat, idx) => {
                    const Icon = stat.icon;
                    const colors = COLOR_MAP[stat.color];
                    return (
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: idx * 0.1 }}
                            key={stat.label}
                            className="group bg-white border border-slate-100 rounded-2xl p-6 space-y-4 hover:border-slate-200 transition-all hover:shadow-lg"
                        >
                            <div className="flex justify-between items-start">
                                <div className={`p-3 rounded-xl ${colors.bg} ${colors.text}`}>
                                    <Icon size={22} />
                                </div>
                                <div className={`flex items-center gap-1 px-2 py-1 rounded-full text-xs font-semibold ${stat.isUp ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'}`}>
                                    {stat.isUp ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
                                    {stat.trend}
                                </div>
                            </div>
                            <div className="space-y-1">
                                <p className="text-xs font-medium text-slate-500">{stat.label}</p>
                                <p className="text-2xl font-bold text-slate-900">{stat.value}</p>
                            </div>
                        </motion.div>
                    );
                })}
            </div>

            {/* Main Analysis Sections */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Chart Section */}
                <div className="lg:col-span-2 bg-white border border-slate-100 rounded-2xl p-6 space-y-6">
                    <div className="flex items-center justify-between">
                        <div className="space-y-1">
                            <h3 className="text-xl font-bold text-slate-900">Biểu đồ tăng trưởng</h3>
                            <p className="text-xs text-slate-500">Thống kê 30 ngày qua</p>
                        </div>
                        <div className="flex bg-slate-100 p-1 rounded-xl gap-1">
                            <button className="px-4 py-2 text-xs font-semibold bg-white rounded-lg shadow-sm text-slate-900">Tuần</button>
                            <button className="px-4 py-2 text-xs font-semibold text-slate-500 hover:text-slate-900 transition-colors">Tháng</button>
                        </div>
                    </div>
                    <div className="aspect-[21/9] w-full bg-slate-50 rounded-xl border-2 border-dashed border-slate-200 flex items-center justify-center">
                        <div className="text-center space-y-3">
                            <div className="p-4 bg-white rounded-xl shadow-lg text-slate-300 inline-block">
                                <BarChart3 size={40} />
                            </div>
                            <p className="text-sm text-slate-400">Đang tải dữ liệu phân tích...</p>
                        </div>
                    </div>
                </div>

                {/* New Orders Section */}
                <div className="bg-white border border-slate-100 rounded-2xl p-6 flex flex-col">
                    <div className="flex items-center justify-between mb-6">
                        <h3 className="text-xl font-bold text-slate-900">Đơn hàng mới</h3>
                        <button className="p-2 bg-slate-50 hover:bg-slate-100 rounded-lg transition-all">
                            <Filter size={16} className="text-slate-400" />
                        </button>
                    </div>
                    <div className="space-y-4 flex-grow">
                        {[1, 2, 3, 4, 5].map((i) => (
                            <div key={i} className="flex items-center justify-between group cursor-pointer hover:bg-slate-50 -mx-3 px-3 py-2 rounded-xl transition-all">
                                <div className="flex items-center gap-4">
                                    <div className="w-10 h-10 rounded-xl bg-blue-50 text-blue-500 flex items-center justify-center font-bold text-xs">
                                        #{1000 + i}
                                    </div>
                                    <div>
                                        <p className="text-sm font-semibold text-slate-900">Khách hàng {i}</p>
                                        <p className="text-xs text-slate-500">{(1200000 * i).toLocaleString()}đ • Chờ xử lý</p>
                                    </div>
                                </div>
                                <div className="p-1.5 bg-slate-50 rounded-lg text-slate-300 group-hover:text-blue-500 group-hover:bg-blue-50 transition-all">
                                    <ChevronRight size={14} />
                                </div>
                            </div>
                        ))}
                    </div>
                    <button className="w-full mt-6 py-3 bg-slate-100 text-slate-600 rounded-xl text-sm font-semibold hover:bg-slate-900 hover:text-white transition-all duration-300">
                        Xem tất cả đơn hàng <ArrowRight size={14} className="inline-block ml-1" />
                    </button>
                </div>
            </div>
        </div>
    );
}
