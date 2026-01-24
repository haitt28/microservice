'use client';

import { useState } from 'react';
import { signIn } from 'next-auth/react';
import { motion } from 'framer-motion';
import { ShieldCheck, Lock, ArrowRight, AlertCircle } from 'lucide-react';

export default function LoginPage() {
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleKeycloakLogin = async () => {
        try {
            setLoading(true);
            setError('');

            const result = await signIn('keycloak', {
                callbackUrl: '/',
                redirect: false
            });

            if (result?.error) {
                setError('Không thể kết nối đến Keycloak. Vui lòng kiểm tra cấu hình.');
            } else if (result?.url) {
                window.location.href = result.url;
            }
        } catch (err) {
            setError('Đã xảy ra lỗi. Vui lòng thử lại sau.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center py-20 px-4 relative overflow-hidden bg-gradient-to-br from-slate-50 via-blue-50/30 to-amber-50/30">
            {/* Gradient Orbs */}
            <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-br from-blue-400/20 to-purple-400/20 rounded-full blur-3xl" />
            <div className="absolute bottom-0 left-0 w-96 h-96 bg-gradient-to-tr from-amber-400/20 to-orange-400/20 rounded-full blur-3xl" />

            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6 }}
                className="w-full max-w-md relative z-10"
            >
                {/* Card */}
                <div className="bg-white rounded-3xl shadow-2xl shadow-slate-200/50 overflow-hidden border border-slate-100">
                    {/* Header */}
                    <div className="bg-gradient-to-br from-slate-900 to-slate-800 p-8 text-center">
                        <h1 className="text-4xl font-black tracking-tighter text-white italic">
                            LUXORA<span className="text-amber-400">.</span>
                        </h1>
                        <p className="text-slate-400 text-sm font-medium mt-2">
                            Hệ thống quản lý tập trung
                        </p>
                    </div>

                    {/* Content */}
                    <div className="p-8 space-y-8">
                        <div className="text-center space-y-2">
                            <h2 className="text-2xl font-bold text-slate-900">Đăng nhập</h2>
                            <p className="text-slate-500 text-sm">
                                Sử dụng tài khoản LUXORA ID của bạn
                            </p>
                        </div>

                        {/* Error Message */}
                        {error && (
                            <motion.div
                                initial={{ opacity: 0, y: -10 }}
                                animate={{ opacity: 1, y: 0 }}
                                className="flex items-start gap-3 p-4 bg-rose-50 border border-rose-200 rounded-2xl"
                            >
                                <AlertCircle className="text-rose-600 flex-shrink-0 mt-0.5" size={20} />
                                <div className="flex-1">
                                    <p className="text-sm font-medium text-rose-900">{error}</p>
                                    <p className="text-xs text-rose-600 mt-1">
                                        Kiểm tra file KEYCLOAK_SETUP.md để cấu hình đúng.
                                    </p>
                                </div>
                            </motion.div>
                        )}

                        {/* Features */}
                        <div className="space-y-4">
                            <div className="flex items-center gap-3 p-4 bg-slate-50 rounded-2xl">
                                <div className="p-2 bg-amber-100 rounded-xl">
                                    <ShieldCheck className="text-amber-600" size={20} />
                                </div>
                                <div>
                                    <p className="text-sm font-bold text-slate-900">Bảo mật cao</p>
                                    <p className="text-xs text-slate-500">Xác thực đa lớp</p>
                                </div>
                            </div>
                            <div className="flex items-center gap-3 p-4 bg-slate-50 rounded-2xl">
                                <div className="p-2 bg-blue-100 rounded-xl">
                                    <Lock className="text-blue-600" size={20} />
                                </div>
                                <div>
                                    <p className="text-sm font-bold text-slate-900">Đăng nhập một lần</p>
                                    <p className="text-xs text-slate-500">Single Sign-On (SSO)</p>
                                </div>
                            </div>
                        </div>

                        {/* Login Button */}
                        <button
                            onClick={handleKeycloakLogin}
                            disabled={loading}
                            className="w-full h-14 bg-gradient-to-r from-slate-900 to-slate-800 hover:from-slate-800 hover:to-slate-700 text-white rounded-2xl font-bold text-sm flex items-center justify-center gap-3 transition-all active:scale-95 shadow-lg shadow-slate-900/20 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {loading ? (
                                <>
                                    <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                                    Đang kết nối...
                                </>
                            ) : (
                                <>
                                    <Lock size={18} />
                                    Đăng nhập với Keycloak
                                    <ArrowRight size={18} className="group-hover:translate-x-1 transition-transform" />
                                </>
                            )}
                        </button>

                        {/* Footer */}
                        <p className="text-center text-xs text-slate-400 leading-relaxed">
                            Bằng cách đăng nhập, bạn đồng ý với{' '}
                            <a href="/terms" className="text-slate-600 hover:text-slate-900 font-medium">
                                Điều khoản dịch vụ
                            </a>{' '}
                            và{' '}
                            <a href="/privacy" className="text-slate-600 hover:text-slate-900 font-medium">
                                Chính sách bảo mật
                            </a>
                        </p>
                    </div>
                </div>

                {/* Bottom Badge */}
                <div className="mt-6 text-center">
                    <p className="text-xs text-slate-400 font-medium">
                        Powered by Keycloak • Enterprise SSO
                    </p>
                </div>
            </motion.div>
        </div>
    );
}
