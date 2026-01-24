'use client';

import { Filter, SlidersHorizontal, Search as SearchIcon, Grid3x3, List } from 'lucide-react';
import ProductCard from '@/components/features/ProductCard';
import { motion } from 'framer-motion';
import { useState } from 'react';
import { Input } from '@/components/ui';

// Mock data with more complete product info
const MOCK_PRODUCTS: any[] = [
    { id: '1', slug: 'luxora-pro-wireless', name: 'LUXORA Pro Wireless Headphones', price: 3500000, salePrice: 2490000, thumbnail: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1000&auto=format&fit=crop', category: { name: 'Âm thanh' }, rating: 4.8, reviewCount: 124 },
    { id: '2', slug: 'minimalist-smart-watch-v2', name: 'Minimalist Smart Watch V2', price: 5200000, thumbnail: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=1000&auto=format&fit=crop', category: { name: 'Phụ kiện' }, rating: 4.5, reviewCount: 89 },
    { id: '3', slug: 'ergonomic-keyboard', name: 'Ergonomic Mechanical Keyboard', price: 1800000, salePrice: 1550000, thumbnail: 'https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?q=80&w=1000&auto=format&fit=crop', category: { name: 'Gaming' }, rating: 4.9, reviewCount: 203 },
    { id: '4', slug: 'smart-home-speaker', name: 'Smart Home Speaker Hub', price: 4500000, thumbnail: 'https://images.unsplash.com/photo-1589003077984-894e133dabab?q=80&w=1000&auto=format&fit=crop', category: { name: 'Nhà thông minh' }, rating: 4.6, reviewCount: 156 },
    { id: '5', slug: 'pro-camera-lens', name: 'Pro Camera Lens 50mm', price: 8900000, thumbnail: 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?q=80&w=1000&auto=format&fit=crop', category: { name: 'Nhiếp ảnh' }, rating: 5.0, reviewCount: 67 },
    { id: '6', slug: 'leather-laptop-bag', name: 'Leather Laptop Bag', price: 1200000, thumbnail: 'https://images.unsplash.com/photo-1547949003-9792a18a2601?q=80&w=1000&auto=format&fit=crop', category: { name: 'Thời trang' }, rating: 4.3, reviewCount: 92 }
];

export default function ProductsPage() {
    const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');

    return (
        <div className="bg-slate-50 min-h-screen py-12">
            <div className="section-container space-y-10">
                {/* Header - Clean, No Italic */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6 bg-white p-8 rounded-3xl shadow-sm">
                    <div className="space-y-2">
                        <h1 className="text-4xl font-bold text-slate-900">Bộ sưu tập</h1>
                        <p className="text-slate-500 font-medium">Khám phá {MOCK_PRODUCTS.length} sản phẩm cao cấp</p>
                    </div>

                    <div className="flex items-center gap-4 w-full md:w-auto">
                        {/* Search */}
                        <div className="flex-1 md:w-80 relative">
                            <SearchIcon className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" size={20} />
                            <Input
                                type="search"
                                placeholder="Tìm kiếm sản phẩm..."
                                className="!pl-12"
                            />
                        </div>

                        {/* View Toggle */}
                        <div className="hidden md:flex bg-slate-100 p-1 rounded-xl">
                            <button
                                onClick={() => setViewMode('grid')}
                                className={`p-2.5 rounded-lg transition-all ${viewMode === 'grid' ? 'bg-white shadow-sm text-slate-900' : 'text-slate-400'}`}
                            >
                                <Grid3x3 size={18} />
                            </button>
                            <button
                                onClick={() => setViewMode('list')}
                                className={`p-2.5 rounded-lg transition-all ${viewMode === 'list' ? 'bg-white shadow-sm text-slate-900' : 'text-slate-400'}`}
                            >
                                <List size={18} />
                            </button>
                        </div>
                    </div>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
                    {/* Filters Sidebar */}
                    <aside className="hidden lg:block space-y-6">
                        <div className="bg-white p-6 rounded-3xl shadow-sm space-y-6">
                            <h3 className="font-bold text-lg flex items-center gap-2 text-slate-900">
                                <Filter size={20} /> Bộ lọc
                            </h3>

                            {/* Categories */}
                            <div className="space-y-4">
                                <p className="text-sm font-bold text-slate-400 uppercase tracking-wider">Danh mục</p>
                                <div className="space-y-3">
                                    {['Tất cả', 'Âm thanh', 'Phụ kiện', 'Gaming', 'Nhà thông minh', 'Nhiếp ảnh'].map((cat) => (
                                        <label key={cat} className="flex items-center gap-3 cursor-pointer group">
                                            <input type="checkbox" className="w-4 h-4 border-slate-300 rounded text-blue-600 focus:ring-blue-500" />
                                            <span className="text-slate-600 group-hover:text-slate-900 transition-colors font-medium">{cat}</span>
                                        </label>
                                    ))}
                                </div>
                            </div>

                            <div className="h-px bg-slate-100" />

                            {/* Price Range */}
                            <div className="space-y-4">
                                <p className="text-sm font-bold text-slate-400 uppercase tracking-wider">Khoảng giá</p>
                                <input type="range" className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-blue-600" />
                                <div className="flex justify-between text-sm text-slate-500 font-medium">
                                    <span>0đ</span>
                                    <span>10.000.000đ+</span>
                                </div>
                            </div>
                        </div>
                    </aside>

                    {/* Product Grid */}
                    <div className="lg:col-span-3 space-y-8">
                        {/* Sort Bar */}
                        <div className="flex items-center justify-between bg-white p-4 rounded-2xl shadow-sm">
                            <p className="text-sm font-medium text-slate-600">
                                Hiển thị <span className="font-bold text-slate-900">{MOCK_PRODUCTS.length}</span> sản phẩm
                            </p>
                            <select className="input-field !w-auto min-w-[140px] !py-2 cursor-pointer">
                                <option>Mới nhất</option>
                                <option>Giá: Thấp đến cao</option>
                                <option>Giá: Cao đến thấp</option>
                                <option>Đánh giá cao nhất</option>
                            </select>
                        </div>

                        {/* Products */}
                        <div className={`grid gap-8 ${viewMode === 'grid' ? 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3' : 'grid-cols-1'}`}>
                            {MOCK_PRODUCTS.map((product, idx) => (
                                <motion.div
                                    key={product.id}
                                    initial={{ opacity: 0, y: 20 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: idx * 0.1 }}
                                >
                                    <ProductCard product={product} />
                                </motion.div>
                            ))}
                        </div>

                        {/* Pagination */}
                        <div className="flex justify-center gap-2 pt-8">
                            {[1, 2, 3, '...', 10].map((p, i) => (
                                <button
                                    key={i}
                                    className={`min-w-[44px] h-11 px-4 rounded-xl font-bold transition-all ${p === 1
                                        ? 'bg-slate-900 text-white shadow-lg'
                                        : p === '...'
                                            ? 'bg-transparent text-slate-400 cursor-default'
                                            : 'bg-white border border-slate-200 text-slate-600 hover:border-slate-300 hover:bg-slate-50'
                                        }`}
                                >
                                    {p}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
