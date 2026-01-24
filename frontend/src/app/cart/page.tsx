'use client';

import Image from 'next/image';
import Link from 'next/link';
import { Trash2, Plus, Minus, ShoppingBag, ArrowRight, Tag } from 'lucide-react';
import { useCartStore } from '@/store/cartStore';
import { motion } from 'framer-motion';

export default function CartPage() {
    const { items, updateQuantity, removeItem, totalPrice, totalItems } = useCartStore();

    if (items.length === 0) {
        return (
            <div className="section-container min-h-[70vh] flex items-center justify-center py-20">
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="text-center space-y-8 max-w-md"
                >
                    <div className="w-40 h-40 bg-gradient-to-br from-slate-100 to-slate-200 rounded-full flex items-center justify-center mx-auto">
                        <ShoppingBag size={80} className="text-slate-400" />
                    </div>
                    <div className="space-y-3">
                        <h2 className="text-4xl font-bold text-slate-900">Giỏ hàng trống</h2>
                        <p className="text-slate-500 text-lg">
                            Bạn chưa có sản phẩm nào trong giỏ hàng. Khám phá bộ sưu tập của chúng tôi ngay!
                        </p>
                    </div>
                    <Link
                        href="/products"
                        className="inline-flex items-center gap-2 px-10 py-5 bg-slate-900 text-white rounded-2xl font-bold hover:bg-slate-800 transition-all shadow-2xl hover:shadow-3xl text-lg"
                    >
                        Khám phá sản phẩm <ArrowRight size={24} />
                    </Link>
                </motion.div>
            </div>
        );
    }

    return (
        <div className="bg-gradient-to-b from-slate-50 to-white min-h-screen py-16">
            <div className="section-container">
                {/* Header */}
                <div className="mb-16 text-center">
                    <h1 className="text-4xl md:text-5xl font-black text-slate-900 mb-4">Giỏ hàng của bạn</h1>
                    <p className="text-lg text-slate-600">Bạn có <span className="font-bold text-slate-900">{totalItems()}</span> sản phẩm trong giỏ hàng</p>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
                    {/* Cart Items */}
                    <div className="lg:col-span-2 space-y-6">
                        {items.map((item, index) => (
                            <motion.div
                                key={item.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.1 }}
                                className="bg-white rounded-3xl p-8 shadow-lg hover:shadow-2xl transition-all border border-slate-100"
                            >
                                <div className="flex gap-8">
                                    {/* Product Image - Larger */}
                                    <div className="relative w-40 h-40 bg-slate-50 rounded-3xl overflow-hidden flex-shrink-0 shadow-md">
                                        <Image
                                            src={item.thumbnail || '/placeholder.png'}
                                            alt={item.name}
                                            fill
                                            className="object-cover"
                                        />
                                    </div>

                                    {/* Product Info */}
                                    <div className="flex-1 space-y-3">
                                        <div className="flex justify-between items-start">
                                            <div>
                                                <Link href={`/products/${item.slug}`} className="font-bold text-lg text-slate-900 hover:text-blue-600 transition-colors">
                                                    {item.name}
                                                </Link>
                                                <p className="text-sm text-slate-500 mt-1">
                                                    {item.category?.name}
                                                </p>
                                            </div>
                                            <button
                                                onClick={() => removeItem(item.id)}
                                                className="p-2 text-slate-400 hover:text-rose-500 hover:bg-rose-50 rounded-xl transition-all"
                                            >
                                                <Trash2 size={20} />
                                            </button>
                                        </div>

                                        {/* Variants */}
                                        {(item.selectedColor || item.selectedSize) && (
                                            <div className="flex gap-4 text-sm">
                                                {item.selectedColor && (
                                                    <span className="text-slate-600">
                                                        Màu: <span className="font-medium text-slate-900">{item.selectedColor}</span>
                                                    </span>
                                                )}
                                                {item.selectedSize && (
                                                    <span className="text-slate-600">
                                                        Size: <span className="font-medium text-slate-900">{item.selectedSize}</span>
                                                    </span>
                                                )}
                                            </div>
                                        )}

                                        {/* Price & Quantity */}
                                        <div className="flex items-center justify-between pt-2">
                                            <div className="flex items-center gap-3">
                                                <span className="text-xl font-bold text-slate-900">
                                                    {((item.salePrice || item.price) * item.quantity).toLocaleString('vi-VN')}đ
                                                </span>
                                                {item.salePrice && (
                                                    <span className="text-sm text-slate-400 line-through">
                                                        {(item.price * item.quantity).toLocaleString('vi-VN')}đ
                                                    </span>
                                                )}
                                            </div>

                                            {/* Quantity Controls */}
                                            <div className="flex items-center border-2 border-slate-200 rounded-xl">
                                                <button
                                                    onClick={() => updateQuantity(item.id, Math.max(1, item.quantity - 1))}
                                                    className="p-2 hover:bg-slate-50 transition-colors"
                                                >
                                                    <Minus size={16} />
                                                </button>
                                                <span className="px-4 font-bold">{item.quantity}</span>
                                                <button
                                                    onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                                    className="p-2 hover:bg-slate-50 transition-colors"
                                                >
                                                    <Plus size={16} />
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </motion.div>
                        ))}
                    </div>

                    {/* Order Summary - Enhanced */}
                    <div className="lg:col-span-1">
                        <div className="bg-white rounded-3xl p-10 shadow-2xl sticky top-24 space-y-8 border border-slate-100">
                            <h3 className="text-2xl font-black text-slate-900">Tổng đơn hàng</h3>

                            {/* Coupon */}
                            <div className="space-y-3">
                                <label className="text-sm font-bold text-slate-700 uppercase tracking-wide">Mã giảm giá</label>
                                <div className="flex gap-3">
                                    <input
                                        type="text"
                                        placeholder="Nhập mã..."
                                        className="flex-1 min-w-0 px-5 py-4 border-2 border-slate-200 rounded-2xl focus:ring-2 focus:ring-blue-500 focus:border-transparent font-medium"
                                    />
                                    <button className="flex-shrink-0 px-6 py-4 bg-slate-900 text-white font-bold rounded-2xl hover:bg-slate-800 transition-all shadow-lg whitespace-nowrap">
                                        Áp dụng
                                    </button>
                                </div>
                            </div>

                            <div className="space-y-4 py-8 border-y-2 border-slate-100">
                                <div className="flex justify-between text-base text-slate-600">
                                    <span className="font-medium">Tạm tính</span>
                                    <span className="font-bold text-slate-900">{totalPrice().toLocaleString('vi-VN')}đ</span>
                                </div>
                                <div className="flex justify-between text-base text-slate-600">
                                    <span className="font-medium">Phí vận chuyển</span>
                                    <span className="font-bold text-emerald-600">Miễn phí</span>
                                </div>
                            </div>

                            <div className="flex justify-between items-baseline pt-4">
                                <span className="text-lg font-bold text-slate-900">Tổng cộng</span>
                                <span className="text-3xl font-black text-slate-900">{totalPrice().toLocaleString('vi-VN')}đ</span>
                            </div>

                            <Link
                                href="/checkout"
                                className="block w-full bg-gradient-to-r from-slate-900 to-slate-800 text-white py-5 rounded-2xl font-bold text-center hover:from-slate-800 hover:to-slate-700 transition-all shadow-2xl hover:shadow-3xl text-lg"
                            >
                                Tiến hành thanh toán
                            </Link>

                            <Link
                                href="/products"
                                className="block w-full text-center text-blue-600 font-bold hover:underline text-lg"
                            >
                                ← Tiếp tục mua sắm
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
