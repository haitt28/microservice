'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import {
    Star, Share2, Heart, ShieldCheck,
    Truck, Clock, ChevronRight, ChevronLeft,
    ShoppingCart, Check, Plus, Minus, Info
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { ProductService } from '@/services/productService';
import { useCartStore } from '@/store/cartStore';
import ProductCard from '@/components/features/ProductCard';

export default function ProductDetailPage() {
    const { slug } = useParams();
    const addItem = useCartStore((state) => state.addItem);
    const [selectedImage, setSelectedImage] = useState(0);
    const [selectedSize, setSelectedSize] = useState('M');
    const [selectedColor, setSelectedColor] = useState('Royal Brown');
    const [quantity, setQuantity] = useState(1);

    const { data: product, isLoading } = useQuery({
        queryKey: ['product', slug],
        queryFn: () => ProductService.getProductBySlug(slug as string),
        enabled: !!slug
    });

    if (isLoading) return (
        <div className="section-container min-h-[60vh] flex items-center justify-center">
            <div className="text-center space-y-4">
                <div className="w-16 h-16 border-4 border-slate-200 border-t-slate-900 rounded-full animate-spin mx-auto" />
                <p className="text-slate-500 font-medium">Đang tải sản phẩm...</p>
            </div>
        </div>
    );

    if (!product) return (
        <div className="section-container min-h-[60vh] flex items-center justify-center">
            <div className="text-center space-y-4">
                <p className="text-2xl font-bold text-slate-900">Sản phẩm không tồn tại</p>
                <Link href="/products" className="text-blue-600 font-bold hover:underline">
                    ← Quay lại danh sách sản phẩm
                </Link>
            </div>
        </div>
    );

    const images = product.images || [product.thumbnail];

    return (
        <div className="bg-white">
            {/* Breadcrumbs */}
            <nav className="section-container py-6 flex items-center gap-2 text-sm">
                <Link href="/" className="text-slate-500 hover:text-slate-900">Trang chủ</Link>
                <ChevronRight size={16} className="text-slate-300" />
                <Link href="/products" className="text-slate-500 hover:text-slate-900">Sản phẩm</Link>
                <ChevronRight size={16} className="text-slate-300" />
                <span className="text-slate-900 font-medium truncate max-w-[200px]">{product.name}</span>
            </nav>

            <div className="section-container grid grid-cols-1 lg:grid-cols-2 gap-16 py-12">
                {/* Left: Large Image Gallery */}
                <div className="space-y-6">
                    {/* Main Image - MUCH LARGER */}
                    <div className="relative aspect-square bg-slate-50 rounded-3xl overflow-hidden group">
                        <AnimatePresence mode="wait">
                            <motion.div
                                key={selectedImage}
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                exit={{ opacity: 0 }}
                                className="w-full h-full p-12"
                            >
                                <Image
                                    src={images[selectedImage]}
                                    alt={product.name}
                                    fill
                                    className="object-contain"
                                    priority
                                />
                            </motion.div>
                        </AnimatePresence>

                        {/* Image Navigation */}
                        {images.length > 1 && (
                            <>
                                <button
                                    onClick={() => setSelectedImage((prev) => (prev - 1 + images.length) % images.length)}
                                    className="absolute left-4 top-1/2 -translate-y-1/2 p-3 bg-white/90 backdrop-blur rounded-full shadow-lg hover:bg-white transition-all opacity-0 group-hover:opacity-100"
                                >
                                    <ChevronLeft size={24} />
                                </button>
                                <button
                                    onClick={() => setSelectedImage((prev) => (prev + 1) % images.length)}
                                    className="absolute right-4 top-1/2 -translate-y-1/2 p-3 bg-white/90 backdrop-blur rounded-full shadow-lg hover:bg-white transition-all opacity-0 group-hover:opacity-100"
                                >
                                    <ChevronRight size={24} />
                                </button>
                            </>
                        )}

                        {/* Action Buttons */}
                        <div className="absolute top-6 right-6 flex flex-col gap-3">
                            <button className="p-3 bg-white rounded-full shadow-lg hover:bg-rose-50 hover:text-rose-500 transition-all">
                                <Heart size={20} />
                            </button>
                            <button className="p-3 bg-white rounded-full shadow-lg hover:bg-blue-50 hover:text-blue-500 transition-all">
                                <Share2 size={20} />
                            </button>
                        </div>
                    </div>

                    {/* Thumbnails */}
                    <div className="grid grid-cols-5 gap-4">
                        {images.map((img: string, i: number) => (
                            <button
                                key={i}
                                onClick={() => setSelectedImage(i)}
                                className={`relative aspect-square rounded-2xl overflow-hidden border-2 transition-all ${selectedImage === i ? 'border-blue-600 ring-4 ring-blue-100' : 'border-transparent bg-slate-50 hover:border-slate-200'
                                    }`}
                            >
                                <Image src={img} alt={`${product.name} ${i + 1}`} fill className="object-cover p-2" />
                            </button>
                        ))}
                    </div>
                </div>

                {/* Right: Product Info - Clean Typography */}
                <div className="flex flex-col space-y-8">
                    {/* Category & Stock */}
                    <div className="flex items-center gap-4">
                        <span className="px-3 py-1 bg-blue-50 text-blue-600 text-xs font-bold uppercase tracking-wider rounded-full">
                            {product.category.name}
                        </span>
                        {product.stock > 0 && (
                            <span className="flex items-center gap-1.5 text-emerald-600 text-sm font-bold">
                                <Check size={16} /> Còn hàng
                            </span>
                        )}
                    </div>

                    {/* Product Name - NO ITALIC */}
                    <h1 className="text-4xl md:text-5xl font-bold text-slate-900 leading-tight">
                        {product.name}
                    </h1>

                    {/* Rating */}
                    {product.rating && (
                        <div className="flex items-center gap-4 pb-6 border-b border-slate-100">
                            <div className="flex items-center gap-2">
                                <div className="flex text-amber-400">
                                    {[...Array(5)].map((_, i) => (
                                        <Star key={i} size={20} fill={i < Math.floor(product.rating) ? "currentColor" : "none"} />
                                    ))}
                                </div>
                                <span className="font-bold text-lg text-slate-900">{product.rating}</span>
                            </div>
                            <span className="text-slate-500">({product.soldCount || 0} đánh giá)</span>
                        </div>
                    )}

                    {/* Price */}
                    <div className="space-y-2">
                        <div className="flex items-baseline gap-4">
                            <span className="text-4xl font-bold text-slate-900">
                                {(product.salePrice || product.price).toLocaleString('vi-VN')}đ
                            </span>
                            {product.salePrice && (
                                <>
                                    <span className="text-xl text-slate-400 line-through">
                                        {product.price.toLocaleString('vi-VN')}đ
                                    </span>
                                    <span className="px-3 py-1 bg-rose-500 text-white text-sm font-bold rounded-full">
                                        -{Math.round(((product.price - product.salePrice) / product.price) * 100)}%
                                    </span>
                                </>
                            )}
                        </div>
                        <p className="text-sm text-slate-500">Đã bao gồm thuế VAT</p>
                    </div>

                    {/* Description */}
                    <p className="text-slate-600 leading-relaxed text-lg">
                        {product.description}
                    </p>

                    {/* Color Selection */}
                    <div className="space-y-4">
                        <div className="flex items-center justify-between">
                            <p className="font-bold text-slate-900">Màu sắc: <span className="text-blue-600">{selectedColor}</span></p>
                        </div>
                        <div className="flex gap-3">
                            {['Royal Brown', 'Cloud White', 'Ocean Blue', 'Deep Black'].map(color => (
                                <button
                                    key={color}
                                    onClick={() => setSelectedColor(color)}
                                    className={`w-16 h-16 rounded-2xl transition-all border-2 ${selectedColor === color ? 'border-blue-600 ring-4 ring-blue-100 scale-110' : 'border-slate-200 hover:border-slate-300'
                                        }`}
                                >
                                    <div className={`w-full h-full rounded-xl ${color === 'Royal Brown' ? 'bg-amber-900' :
                                            color === 'Cloud White' ? 'bg-slate-100' :
                                                color === 'Ocean Blue' ? 'bg-blue-600' : 'bg-slate-900'
                                        }`} />
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* Size Selection */}
                    <div className="space-y-4">
                        <div className="flex items-center justify-between">
                            <p className="font-bold text-slate-900">Kích thước: <span className="text-blue-600">{selectedSize}</span></p>
                            <Link href="/size-guide" className="text-sm text-blue-600 font-bold hover:underline">
                                Hướng dẫn chọn size
                            </Link>
                        </div>
                        <div className="grid grid-cols-5 gap-3">
                            {['S', 'M', 'L', 'XL', 'XXL'].map(size => (
                                <button
                                    key={size}
                                    onClick={() => setSelectedSize(size)}
                                    className={`h-14 border-2 rounded-2xl font-bold transition-all ${selectedSize === size
                                            ? 'border-slate-900 bg-slate-900 text-white shadow-lg'
                                            : 'border-slate-200 hover:border-slate-300 text-slate-600'
                                        }`}
                                >
                                    {size}
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* Quantity */}
                    <div className="space-y-4">
                        <p className="font-bold text-slate-900">Số lượng</p>
                        <div className="flex items-center gap-4">
                            <div className="flex items-center border-2 border-slate-200 rounded-2xl">
                                <button
                                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                    className="p-4 hover:bg-slate-50 transition-colors"
                                >
                                    <Minus size={20} />
                                </button>
                                <span className="px-8 font-bold text-lg">{quantity}</span>
                                <button
                                    onClick={() => setQuantity(quantity + 1)}
                                    className="p-4 hover:bg-slate-50 transition-colors"
                                >
                                    <Plus size={20} />
                                </button>
                            </div>
                            <p className="text-sm text-slate-500">Còn {product.stock} sản phẩm</p>
                        </div>
                    </div>

                    {/* Add to Cart */}
                    <div className="flex gap-4 pt-4">
                        <button
                            onClick={() => addItem({ ...product, quantity, selectedSize, selectedColor })}
                            className="flex-1 bg-slate-900 text-white py-5 rounded-2xl font-bold text-lg hover:bg-slate-800 transition-all shadow-xl hover:shadow-2xl flex items-center justify-center gap-3"
                        >
                            <ShoppingCart size={24} />
                            Thêm vào giỏ hàng
                        </button>
                        <button className="px-8 bg-amber-400 text-white rounded-2xl font-bold hover:bg-amber-500 transition-all shadow-xl">
                            Mua ngay
                        </button>
                    </div>

                    {/* Trust Badges */}
                    <div className="grid grid-cols-2 gap-4 pt-6 border-t border-slate-100">
                        <div className="flex items-center gap-3 p-4 rounded-2xl bg-slate-50">
                            <div className="p-3 bg-white rounded-xl shadow-sm">
                                <Truck size={24} className="text-blue-600" />
                            </div>
                            <div>
                                <p className="font-bold text-slate-900 text-sm">Giao hàng 2h</p>
                                <p className="text-xs text-slate-500">Miễn phí nội thành</p>
                            </div>
                        </div>
                        <div className="flex items-center gap-3 p-4 rounded-2xl bg-slate-50">
                            <div className="p-3 bg-white rounded-xl shadow-sm">
                                <ShieldCheck size={24} className="text-emerald-600" />
                            </div>
                            <div>
                                <p className="font-bold text-slate-900 text-sm">Bảo hành 2 năm</p>
                                <p className="text-xs text-slate-500">Chính hãng LUXORA</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Related Products */}
            <section className="section-container py-20 border-t border-slate-100">
                <h2 className="text-3xl font-bold text-slate-900 mb-12">Sản phẩm tương tự</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                    {/* Mock related products */}
                    {[1, 2, 3, 4].map(i => (
                        <div key={i} className="aspect-[4/3] bg-slate-50 rounded-3xl" />
                    ))}
                </div>
            </section>
        </div>
    );
}
