'use client';

import { Ticket, Zap, Gift, Timer, ArrowRight, Copy, Check, Percent } from 'lucide-react';
import { motion } from 'framer-motion';
import { useState } from 'react';
import Link from 'next/link';

const COUPONS = [
    { code: 'LUXORANEW', discount: '50.000đ', label: 'Cho đơn hàng đầu tiên', color: 'blue', expires: '31/12/2024' },
    { code: 'TECHWEEK', discount: '10%', label: 'Giảm tối đa 200k', color: 'purple', expires: '15/02/2024' },
    { code: 'FREESHIP', discount: 'MIỄN PHÍ', label: 'Vận chuyển toàn quốc', color: 'emerald', expires: '28/02/2024' },
    { code: 'FLASH50', discount: '50%', label: 'Flash sale cuối tuần', color: 'rose', expires: '25/01/2024' },
];

export default function DealsPage() {
    const [copiedCode, setCopiedCode] = useState<string | null>(null);

    const copyCode = (code: string) => {
        navigator.clipboard.writeText(code);
        setCopiedCode(code);
        setTimeout(() => setCopiedCode(null), 2000);
    };

    return (
        <div className="bg-white min-h-screen">
            {/* Hero */}
            <section className="bg-gradient-to-br from-amber-50 to-orange-50 py-20">
                <div className="section-container text-center space-y-6">
                    <motion.div
                        initial={{ opacity: 0, scale: 0.9 }}
                        animate={{ opacity: 1, scale: 1 }}
                        className="inline-flex items-center gap-2 px-4 py-2 bg-amber-100 text-amber-600 rounded-full text-sm font-bold"
                    >
                        <Percent size={16} />
                        Ưu đãi đặc biệt
                    </motion.div>
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">
                        Khuyến mãi & Ưu đãi
                    </h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Tiết kiệm hơn với các mã giảm giá và chương trình khuyến mãi hấp dẫn
                    </p>
                </div>
            </section>

            {/* Active Coupons */}
            <section className="section-container py-20">
                <h2 className="text-3xl font-bold text-slate-900 mb-12">Mã giảm giá đang hoạt động</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {COUPONS.map((coupon, idx) => (
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: idx * 0.1 }}
                            key={coupon.code}
                            className="relative bg-white border-2 border-slate-100 rounded-3xl p-8 hover:border-slate-200 hover:shadow-xl transition-all overflow-hidden group"
                        >
                            {/* Decorative circles */}
                            <div className="absolute -left-4 top-1/2 -translate-y-1/2 w-8 h-8 bg-slate-50 rounded-full" />
                            <div className="absolute -right-4 top-1/2 -translate-y-1/2 w-8 h-8 bg-slate-50 rounded-full" />

                            <div className="flex items-start justify-between mb-6">
                                <div className={`p-4 rounded-2xl bg-${coupon.color}-50`}>
                                    <Ticket className={`text-${coupon.color}-600`} size={32} />
                                </div>
                                <div className="text-right">
                                    <div className="flex items-center gap-1 text-xs text-slate-500 mb-1">
                                        <Timer size={12} />
                                        HSD: {coupon.expires}
                                    </div>
                                </div>
                            </div>

                            <div className="space-y-4">
                                <div>
                                    <p className="text-4xl font-bold text-slate-900 mb-2">{coupon.discount}</p>
                                    <p className="text-slate-600">{coupon.label}</p>
                                </div>

                                <div className="flex items-center gap-3 p-4 bg-slate-50 rounded-2xl border-2 border-dashed border-slate-200">
                                    <code className="flex-1 text-lg font-bold text-slate-900 tracking-wider">
                                        {coupon.code}
                                    </code>
                                    <button
                                        onClick={() => copyCode(coupon.code)}
                                        className={`px-4 py-2 rounded-xl font-bold text-sm transition-all ${copiedCode === coupon.code
                                                ? 'bg-emerald-500 text-white'
                                                : 'bg-slate-900 text-white hover:bg-slate-800'
                                            }`}
                                    >
                                        {copiedCode === coupon.code ? (
                                            <span className="flex items-center gap-1">
                                                <Check size={16} /> Đã sao
                                            </span>
                                        ) : (
                                            <span className="flex items-center gap-1">
                                                <Copy size={16} /> Sao chép
                                            </span>
                                        )}
                                    </button>
                                </div>
                            </div>
                        </motion.div>
                    ))}
                </div>
            </section>

            {/* Featured Campaign */}
            <section className="section-container py-20">
                <div className="relative rounded-[48px] overflow-hidden bg-gradient-to-br from-slate-900 via-blue-900 to-purple-900 p-16 md:p-24">
                    <div className="absolute inset-0 opacity-20">
                        <div className="absolute top-0 left-0 w-96 h-96 bg-blue-500 rounded-full blur-3xl" />
                        <div className="absolute bottom-0 right-0 w-96 h-96 bg-purple-500 rounded-full blur-3xl" />
                    </div>

                    <div className="relative z-10 grid grid-cols-1 lg:grid-cols-2 items-center gap-12">
                        <div className="space-y-8 text-white">
                            <div className="flex items-center gap-4">
                                <span className="px-4 py-2 bg-amber-400 text-slate-900 text-sm font-bold rounded-full uppercase">
                                    Hot Event
                                </span>
                                <span className="flex items-center gap-2 text-slate-300 text-sm font-bold">
                                    <Timer size={16} /> Còn 2 ngày 12 giờ
                                </span>
                            </div>

                            <h2 className="text-5xl md:text-6xl font-bold leading-tight">
                                TECH REVOLUTION <br />
                                <span className="text-amber-400">2024</span>
                            </h2>

                            <p className="text-xl text-slate-300 leading-relaxed">
                                Bùng nổ ưu đãi lên đến 70% dành riêng cho các tín đồ công nghệ.
                                Sở hữu ngay những siêu phẩm hàng đầu với mức giá không tưởng.
                            </p>

                            <Link
                                href="/products"
                                className="inline-flex items-center gap-3 px-8 py-4 bg-amber-400 text-slate-900 rounded-2xl font-bold text-lg hover:bg-amber-500 transition-all shadow-2xl"
                            >
                                Tham gia ngay <ArrowRight size={24} />
                            </Link>
                        </div>

                        <div className="hidden lg:flex justify-end">
                            <div className="relative w-96 h-96">
                                <div className="absolute inset-0 bg-amber-400/20 rounded-[64px] rotate-6 animate-pulse" />
                                <div className="absolute inset-0 bg-white/5 backdrop-blur-3xl rounded-[64px] border border-white/10 flex items-center justify-center">
                                    <Zap className="text-amber-400" size={120} fill="currentColor" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}
