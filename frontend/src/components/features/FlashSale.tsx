'use client';

import { useState, useEffect } from 'react';
import { Timer, Zap, ArrowRight, ArrowLeft } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import ProductCard from '@/components/features/ProductCard';

const FLASH_PRODUCTS = [
    { id: 'f1', name: 'Premium Audio Set', price: 12000000, salePrice: 7990000, thumbnail: 'https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=1000&auto=format&fit=crop', category: { name: 'Âm thanh' } },
    { id: 'f2', name: 'Smart Home Hub v3', price: 4500000, salePrice: 2890000, thumbnail: 'https://images.unsplash.com/photo-1589003077984-894e133dabab?q=80&w=1000&auto=format&fit=crop', category: { name: 'Gia dụng' } },
    { id: 'f3', name: 'Gaming Chair Elite', price: 8500000, salePrice: 5200000, thumbnail: 'https://images.unsplash.com/photo-1598550476439-6847785fce66?q=80&w=1000&auto=format&fit=crop', category: { name: 'Nội thất' } },
    { id: 'f4', name: 'Ultrawide Monitor 49"', price: 25000000, salePrice: 19900000, thumbnail: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=1000&auto=format&fit=crop', category: { name: 'Màn hình' } },
];

export default function FlashSale() {
    const [timeLeft, setTimeLeft] = useState({ hours: 12, minutes: 59, seconds: 59 });

    useEffect(() => {
        const timer = setInterval(() => {
            setTimeLeft(prev => {
                if (prev.seconds > 0) return { ...prev, seconds: prev.seconds - 1 };
                if (prev.minutes > 0) return { ...prev, minutes: prev.minutes - 1, seconds: 59 };
                if (prev.hours > 0) return { ...prev, hours: prev.hours - 1, minutes: 59, seconds: 59 };
                return prev;
            });
        }, 1000);
        return () => clearInterval(timer);
    }, []);

    return (
        <section className="section-container">
            <div className="glass-card overflow-hidden border-2 border-accent/20">
                <div className="bg-primary p-8 md:p-12 flex flex-col md:flex-row items-center justify-between gap-8">
                    <div className="flex items-center gap-6">
                        <div className="w-20 h-20 bg-accent text-white rounded-[32px] flex items-center justify-center shadow-xl shadow-amber-500/20 rotate-3 animate-pulse">
                            <Zap size={40} fill="currentColor" />
                        </div>
                        <div className="space-y-2">
                            <h2 className="text-3xl md:text-5xl font-black text-white tracking-tighter uppercase italic">FLASH SALE</h2>
                            <p className="text-slate-400 font-bold uppercase tracking-widest text-xs flex items-center gap-2">
                                <Timer size={14} className="text-accent" /> Kết thúc sau
                            </p>
                        </div>
                    </div>

                    <div className="flex gap-4">
                        {[timeLeft.hours, timeLeft.minutes, timeLeft.seconds].map((unit, i) => (
                            <div key={i} className="flex items-center gap-2">
                                <div className="bg-white/10 backdrop-blur-md w-16 h-20 rounded-2xl flex items-center justify-center text-3xl font-black text-white border border-white/10">
                                    {unit.toString().padStart(2, '0')}
                                </div>
                                {i < 2 && <span className="text-3xl font-black text-accent">:</span>}
                            </div>
                        ))}
                    </div>
                </div>

                <div className="p-10 bg-slate-50/50">
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                        {FLASH_PRODUCTS.map((product) => (
                            <div key={product.id} className="relative group">
                                <ProductCard product={product as any} />
                                <div className="absolute top-4 right-4 bg-primary text-white text-[10px] font-black px-2 py-1 rounded-md z-20">
                                    CHỈ CÒN 3 SẢN PHẨM
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="mt-12 text-center">
                        <button className="inline-flex items-center gap-3 text-slate-400 hover:text-primary font-black uppercase tracking-tighter transition-all group">
                            Xem toàn bộ ưu đãi <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
                        </button>
                    </div>
                </div>
            </div>
        </section>
    );
}
