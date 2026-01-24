'use client';

import Image from 'next/image';
import Link from 'next/link';
import { Product } from '@/types';
import { ShoppingCart, Heart, Eye, Star } from 'lucide-react';
import { useCartStore } from '@/store/cartStore';
import { motion } from 'framer-motion';

interface ProductCardProps {
    product: Product;
}

export default function ProductCard({ product }: ProductCardProps) {
    const addItem = useCartStore((state) => state.addItem);
    const discount = product.salePrice
        ? Math.round(((product.price - product.salePrice) / product.price) * 100)
        : 0;

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-50px" }}
            transition={{ duration: 0.4 }}
            className="group bg-white border border-slate-100 rounded-3xl overflow-hidden hover:shadow-2xl hover:border-slate-200 transition-all duration-500"
        >
            {/* Large Image Container */}
            <Link href={`/products/${product.slug}`} className="relative block aspect-[4/3] bg-slate-50 overflow-hidden">
                <Image
                    src={product.thumbnail || '/placeholder.png'}
                    alt={product.name}
                    fill
                    className="object-cover group-hover:scale-105 transition-transform duration-700"
                    sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                />

                {/* Discount Badge */}
                {discount > 0 && (
                    <div className="absolute top-4 left-4 bg-rose-500 text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-lg">
                        -{discount}%
                    </div>
                )}

                {/* Hover Actions */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                    <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
                        <button className="p-3 bg-white rounded-full hover:bg-amber-400 hover:text-white transition-all shadow-lg">
                            <Heart size={18} />
                        </button>
                        <button className="p-3 bg-white rounded-full hover:bg-amber-400 hover:text-white transition-all shadow-lg">
                            <Eye size={18} />
                        </button>
                    </div>
                </div>
            </Link>

            {/* Product Info */}
            <div className="p-4 space-y-2 flex flex-col">
                {/* Category - Softer styling */}
                <p className="text-[10px] font-semibold text-blue-600 uppercase tracking-wide">
                    {product.category?.name || '\u00A0'}
                </p>

                {/* Product Name - Medium weight instead of bold */}
                <Link href={`/products/${product.slug}`}>
                    <h3 className="text-base font-semibold text-slate-900 line-clamp-2 leading-tight hover:text-blue-600 transition-colors min-h-[2.5rem]">
                        {product.name}
                    </h3>
                </Link>


                {/* Rating - Show placeholder when no rating */}
                <div className="flex items-center gap-2 min-h-[20px]">
                    {product.rating && product.rating > 0 ? (
                        <>
                            <div className="flex text-amber-400">
                                {[...Array(5)].map((_, i) => (
                                    <Star
                                        key={i}
                                        size={13}
                                        fill={i < Math.floor(product.rating) ? "currentColor" : "none"}
                                    />
                                ))}
                            </div>
                            <span className="text-[11px] text-slate-500 font-medium">
                                ({product.reviewCount || 0})
                            </span>
                        </>
                    ) : (
                        <span className="text-[11px] text-slate-400 italic">
                            Chưa có đánh giá
                        </span>
                    )}
                </div>

                {/* Price - Reduced size and better alignment */}
                <div className="flex items-baseline gap-2 pt-1">
                    <span className="text-xl font-bold text-slate-900">
                        {(product.salePrice || product.price).toLocaleString('vi-VN')}đ
                    </span>
                    {product.salePrice && (
                        <span className="text-xs text-slate-400 line-through">
                            {product.price.toLocaleString('vi-VN')}đ
                        </span>
                    )}
                </div>

                {/* Add to Cart Button - Reduced padding */}
                <button
                    onClick={(e) => {
                        e.preventDefault();
                        addItem(product);
                    }}
                    className="w-full bg-slate-900 text-white py-3 rounded-xl text-sm font-semibold hover:bg-amber-500 transition-all duration-300 flex items-center justify-center gap-2 shadow-md hover:shadow-lg mt-auto"
                >
                    <ShoppingCart size={16} />
                    Thêm vào giỏ
                </button>
            </div>
        </motion.div>
    );
}
