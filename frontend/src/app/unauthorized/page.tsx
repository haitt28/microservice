'use client';

import { motion } from 'framer-motion';
import { ShieldAlert, Home, ArrowLeft } from 'lucide-react';
import Link from 'next/link';
import { useSession } from 'next-auth/react';

export default function UnauthorizedPage() {
    const { data: session } = useSession();

    return (
        <div className="min-h-screen flex items-center justify-center py-20 px-4 bg-gradient-to-br from-slate-50 via-red-50/30 to-orange-50/30">
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6 }}
                className="max-w-2xl w-full"
            >
                <div className="bg-white rounded-3xl shadow-2xl shadow-slate-200/50 overflow-hidden border border-slate-100 p-12">
                    {/* Icon */}
                    <div className="flex justify-center mb-8">
                        <div className="p-6 bg-red-100 rounded-full">
                            <ShieldAlert className="text-red-600" size={64} />
                        </div>
                    </div>

                    {/* Content */}
                    <div className="text-center space-y-6">
                        <div className="space-y-2">
                            <h1 className="text-4xl font-black text-slate-900">
                                Truy cập bị từ chối
                            </h1>
                            <p className="text-xl text-slate-600 font-medium">
                                Bạn không có quyền truy cập trang này
                            </p>
                        </div>

                        <div className="bg-slate-50 rounded-2xl p-6 space-y-3">
                            <p className="text-sm text-slate-700">
                                Trang quản trị chỉ dành cho người dùng có quyền <span className="font-bold text-red-600">Administrator</span>.
                            </p>
                            {session?.user?.email && (
                                <p className="text-xs text-slate-500">
                                    Tài khoản hiện tại: <span className="font-bold">{session.user.email}</span>
                                </p>
                            )}
                            <p className="text-xs text-slate-500">
                                Nếu bạn cho rằng đây là lỗi, vui lòng liên hệ quản trị viên hệ thống.
                            </p>
                        </div>

                        {/* Actions */}
                        <div className="flex flex-col sm:flex-row gap-4 justify-center pt-4">
                            <Link
                                href="/"
                                className="inline-flex items-center justify-center gap-2 px-8 py-4 bg-gradient-to-r from-slate-900 to-slate-800 hover:from-slate-800 hover:to-slate-700 text-white rounded-2xl font-bold text-sm transition-all active:scale-95 shadow-lg shadow-slate-900/20"
                            >
                                <Home size={18} />
                                Về trang chủ
                            </Link>
                            <button
                                onClick={() => window.history.back()}
                                className="inline-flex items-center justify-center gap-2 px-8 py-4 bg-white hover:bg-slate-50 text-slate-900 border-2 border-slate-200 rounded-2xl font-bold text-sm transition-all active:scale-95"
                            >
                                <ArrowLeft size={18} />
                                Quay lại
                            </button>
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <div className="mt-8 text-center">
                    <p className="text-sm text-slate-500">
                        Cần hỗ trợ? Liên hệ:{' '}
                        <a href="/contact" className="text-slate-700 hover:text-slate-900 font-medium">
                            support@luxora.vn
                        </a>
                    </p>
                </div>
            </motion.div>
        </div>
    );
}
