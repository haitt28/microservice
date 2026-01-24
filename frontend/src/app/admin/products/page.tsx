'use client';

import { useState } from 'react';
import {
    Package, Search, Plus, MoreVertical,
    Trash2, Edit3, Image as ImageIcon
} from 'lucide-react';

const PRODUCTS = [
    { id: '1', name: 'Fiinx Pro Wireless Headphones', sku: 'HP-PRO-001', stock: 45, price: 2490000, category: 'Âm thanh' },
    { id: '2', name: 'Minimalist Smart Watch V2', sku: 'SW-MINI-002', stock: 120, price: 5200000, category: 'Phụ kiện' },
    { id: '3', name: 'Mechanical Keyboard X', sku: 'KB-X-003', stock: 12, price: 1800000, category: 'Gaming' },
];

export default function AdminProductsPage() {
    return (
        <div className="space-y-8">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-black text-slate-800 tracking-tight">Sản phẩm</h1>
                <button className="btn-accent flex items-center gap-2">
                    <Plus size={20} /> Thêm sản phẩm
                </button>
            </div>

            <div className="glass-card overflow-hidden">
                {/* Table Filters */}
                <div className="p-6 border-b border-slate-100 flex gap-4">
                    <div className="relative flex-1">
                        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                        <input
                            type="text"
                            placeholder="Tìm theo tên hoặc SKU..."
                            className="w-full bg-slate-50 border-none rounded-xl px-12 py-3 text-sm focus:ring-1 focus:ring-accent"
                        />
                    </div>
                    <select className="bg-slate-50 border-none rounded-xl px-6 py-3 text-sm focus:ring-1 focus:ring-accent">
                        <option>Tất cả danh mục</option>
                        <option>Âm thanh</option>
                        <option>Phụ kiện</option>
                    </select>
                </div>

                {/* Products Table */}
                <table className="w-full text-left">
                    <thead className="bg-slate-50/50 text-slate-500 uppercase text-[10px] font-black tracking-widest">
                        <tr>
                            <th className="px-8 py-4">Sản phẩm</th>
                            <th className="px-8 py-4">SKU</th>
                            <th className="px-8 py-4">Kho</th>
                            <th className="px-8 py-4">Giá bán</th>
                            <th className="px-8 py-4 text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {PRODUCTS.map((product) => (
                            <tr key={product.id} className="group hover:bg-slate-50 transition-colors">
                                <td className="px-8 py-5">
                                    <div className="flex items-center gap-4">
                                        <div className="w-12 h-12 bg-slate-100 rounded-xl flex items-center justify-center text-slate-300">
                                            <ImageIcon size={20} />
                                        </div>
                                        <div>
                                            <p className="font-bold text-slate-800">{product.name}</p>
                                            <p className="text-[10px] text-slate-400">{product.category}</p>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-8 py-5 text-sm font-medium text-slate-500">{product.sku}</td>
                                <td className="px-8 py-5">
                                    <div className={`text-xs font-bold ${product.stock < 15 ? 'text-error' : 'text-slate-600'}`}>
                                        {product.stock} đơn vị
                                        {product.stock < 15 && <span className="ml-2 px-2 py-0.5 bg-red-50 text-[8px] uppercase">Sắp hết</span>}
                                    </div>
                                </td>
                                <td className="px-8 py-5 font-black text-primary">{product.price.toLocaleString()}đ</td>
                                <td className="px-8 py-5">
                                    <div className="flex items-center justify-center gap-2">
                                        <button className="p-2 text-slate-400 hover:text-primary hover:bg-blue-50 rounded-lg transition-all">
                                            <Edit3 size={18} />
                                        </button>
                                        <button className="p-2 text-slate-400 hover:text-error hover:bg-red-50 rounded-lg transition-all">
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {/* Pagination */}
                <div className="p-6 bg-slate-50/30 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400 font-bold">
                    <p>Hiển thị 3 trên 850 sản phẩm</p>
                    <div className="flex gap-2">
                        <button className="px-4 py-2 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 disabled:opacity-50">Trước</button>
                        <button className="px-4 py-2 bg-white border border-slate-200 rounded-lg hover:bg-slate-50">Sau</button>
                    </div>
                </div>
            </div>
        </div>
    );
}
