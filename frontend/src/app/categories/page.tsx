'use client';

import {
    AudioLines, Smartphone, Gamepad2,
    Watch, Camera, Laptop, TrendingUp, Sparkles
} from 'lucide-react';
import { motion } from 'framer-motion';
import Link from 'next/link';

const CATEGORIES = [
    { name: 'Âm thanh', icon: AudioLines, count: 120, color: 'blue', gradient: 'from-blue-500 to-cyan-500' },
    { name: 'Điện thoại', icon: Smartphone, count: 85, color: 'emerald', gradient: 'from-emerald-500 to-teal-500' },
    { name: 'Gaming', icon: Gamepad2, count: 64, color: 'purple', gradient: 'from-purple-500 to-pink-500' },
    { name: 'Phụ kiện', icon: Watch, count: 210, color: 'amber', gradient: 'from-amber-500 to-orange-500' },
    { name: 'Nhiếp ảnh', icon: Camera, count: 42, color: 'rose', gradient: 'from-rose-500 to-red-500' },
    { name: 'Máy tính', icon: Laptop, count: 56, color: 'slate', gradient: 'from-slate-500 to-gray-500' },
];

export default function CategoriesPage() {
    return (
        <div className="bg-white min-h-screen">
            {/* Hero Section */}
            <section className="bg-gradient-to-br from-slate-50 to-blue-50/30 py-20">
                <div className="section-container text-center space-y-6">
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="inline-flex items-center gap-2 px-4 py-2 bg-blue-100 text-blue-600 rounded-full text-sm font-bold"
                    >
                        <Sparkles size={16} />
                        Khám phá theo danh mục
                    </motion.div>
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">
                        Danh mục sản phẩm
                    </h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Tìm kiếm sản phẩm yêu thích của bạn qua các danh mục được tuyển chọn kỹ lưỡng
                    </p>
                </div>
            </section>

            {/* Categories Grid */}
            <section className="section-container py-20">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
                    {CATEGORIES.map((cat, idx) => {
                        const Icon = cat.icon;
                        return (
                            <motion.div
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: idx * 0.1 }}
                                key={cat.name}
                            >
                                <Link
                                    href={`/products?category=${cat.name}`}
                                    className="group block bg-white border-2 border-slate-100 rounded-3xl p-8 hover:border-slate-200 hover:shadow-2xl transition-all duration-300"
                                >
                                    {/* Large Icon with Gradient */}
                                    <div className={`inline-flex p-6 rounded-3xl bg-gradient-to-br ${cat.gradient} mb-6 group-hover:scale-110 transition-transform duration-300 shadow-lg`}>
                                        <Icon size={48} className="text-white" />
                                    </div>

                                    {/* Category Info */}
                                    <div className="space-y-2">
                                        <h3 className="text-2xl font-bold text-slate-900 group-hover:text-blue-600 transition-colors">
                                            {cat.name}
                                        </h3>
                                        <p className="text-slate-500 font-medium">
                                            {cat.count} sản phẩm
                                        </p>
                                    </div>

                                    {/* Trending Badge */}
                                    {idx < 3 && (
                                        <div className="mt-4 inline-flex items-center gap-1 px-3 py-1 bg-amber-50 text-amber-600 rounded-full text-xs font-bold">
                                            <TrendingUp size={12} />
                                            Trending
                                        </div>
                                    )}
                                </Link>
                            </motion.div>
                        );
                    })}
                </div>
            </section>

            {/* CTA Section */}
            <section className="section-container py-20">
                <div className="bg-gradient-to-br from-slate-900 to-slate-800 rounded-[48px] p-16 text-center text-white">
                    <h2 className="text-4xl font-bold mb-4">Không tìm thấy danh mục phù hợp?</h2>
                    <p className="text-slate-300 text-lg mb-8 max-w-2xl mx-auto">
                        Khám phá toàn bộ bộ sưu tập của chúng tôi hoặc liên hệ với đội ngũ hỗ trợ
                    </p>
                    <div className="flex flex-col sm:flex-row gap-4 justify-center">
                        <Link
                            href="/products"
                            className="px-8 py-4 bg-white text-slate-900 rounded-2xl font-bold hover:bg-slate-100 transition-all"
                        >
                            Xem tất cả sản phẩm
                        </Link>
                        <Link
                            href="/contact"
                            className="px-8 py-4 bg-white/10 border-2 border-white/20 text-white rounded-2xl font-bold hover:bg-white/20 transition-all"
                        >
                            Liên hệ hỗ trợ
                        </Link>
                    </div>
                </div>
            </section>
        </div>
    );
}
