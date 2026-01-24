'use client';

import Link from 'next/link';
import { motion } from 'framer-motion';
import { User, Mail, Lock, ArrowRight, CheckCircle2 } from 'lucide-react';

export default function RegisterPage() {
    return (
        <div className="section-container min-h-[80vh] flex items-center justify-center py-20">
            <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                className="glass-card w-full max-w-lg p-10 space-y-10"
            >
                <div className="text-center space-y-2">
                    <h1 className="text-4xl font-black tracking-tighter uppercase italic">Tham gia với LUXORA</h1>
                    <p className="text-slate-400">Tạo tài khoản để nhận ưu đãi và quản lý đơn hàng.</p>
                </div>

                <form className="space-y-5">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <label className="text-sm font-bold text-slate-700 ml-1">Họ</label>
                            <input
                                type="text"
                                placeholder="Nguyễn"
                                className="w-full bg-slate-50 border border-slate-200 rounded-2xl px-6 py-4 focus:ring-2 focus:ring-primary transition-all"
                            />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-bold text-slate-700 ml-1">Tên</label>
                            <input
                                type="text"
                                placeholder="Văn An"
                                className="w-full bg-slate-50 border border-slate-200 rounded-2xl px-6 py-4 focus:ring-2 focus:ring-primary transition-all"
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-bold text-slate-700 ml-1">Email</label>
                        <div className="relative">
                            <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={20} />
                            <input
                                type="email"
                                placeholder="email@example.com"
                                className="w-full bg-slate-50 border border-slate-200 rounded-2xl px-12 py-4 focus:ring-2 focus:ring-primary transition-all"
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-bold text-slate-700 ml-1">Mật khẩu</label>
                        <div className="relative">
                            <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={20} />
                            <input
                                type="password"
                                placeholder="••••••••"
                                className="w-full bg-slate-50 border border-slate-200 rounded-2xl px-12 py-4 focus:ring-2 focus:ring-primary transition-all"
                            />
                        </div>
                    </div>

                    <div className="p-4 bg-emerald-50 rounded-2xl space-y-2">
                        <div className="flex items-center gap-2 text-success text-xs font-bold">
                            <CheckCircle2 size={14} /> Bảo mật 256-bit chuẩn quốc tế
                        </div>
                        <p className="text-[10px] text-slate-500">Bằng việc đăng ký, bạn đồng ý với Điều khoản dịch vụ và Chính sách bảo mật của chúng tôi.</p>
                    </div>

                    <button className="w-full btn-accent h-14 flex items-center justify-center gap-2 group shadow-xl shadow-amber-500/20 mt-4">
                        Tạo tài khoản <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
                    </button>
                </form>

                <p className="text-center text-sm text-slate-500">
                    Đã có tài khoản? <Link href="/login" className="text-primary font-bold hover:underline">Đăng nhập</Link>
                </p>
            </motion.div>
        </div>
    );
}
