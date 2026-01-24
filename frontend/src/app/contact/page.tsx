'use client';

import { Mail, Phone, MapPin, Clock, Send, MessageSquare } from 'lucide-react';
import { motion } from 'framer-motion';
import { useState } from 'react';

export default function ContactPage() {
    const [formData, setFormData] = useState({ name: '', email: '', subject: '', message: '' });

    return (
        <div className="bg-white min-h-screen">
            {/* Hero */}
            <section className="bg-gradient-to-br from-blue-50 to-slate-50 py-20">
                <div className="section-container text-center space-y-6">
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">Liên hệ với chúng tôi</h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Đội ngũ hỗ trợ của LUXORA luôn sẵn sàng giúp đỡ bạn
                    </p>
                </div>
            </section>

            <div className="section-container py-20">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
                    {/* Contact Info */}
                    <div className="space-y-8">
                        <div>
                            <h2 className="text-2xl font-bold text-slate-900 mb-6">Thông tin liên hệ</h2>
                            <div className="space-y-6">
                                <div className="flex items-start gap-4">
                                    <div className="p-3 bg-blue-50 rounded-xl">
                                        <Phone size={24} className="text-blue-600" />
                                    </div>
                                    <div>
                                        <p className="font-bold text-slate-900">Hotline</p>
                                        <p className="text-slate-600">1900 xxxx</p>
                                    </div>
                                </div>
                                <div className="flex items-start gap-4">
                                    <div className="p-3 bg-emerald-50 rounded-xl">
                                        <Mail size={24} className="text-emerald-600" />
                                    </div>
                                    <div>
                                        <p className="font-bold text-slate-900">Email</p>
                                        <p className="text-slate-600">support@luxora.vn</p>
                                    </div>
                                </div>
                                <div className="flex items-start gap-4">
                                    <div className="p-3 bg-amber-50 rounded-xl">
                                        <MapPin size={24} className="text-amber-600" />
                                    </div>
                                    <div>
                                        <p className="font-bold text-slate-900">Địa chỉ</p>
                                        <p className="text-slate-600">123 Đường ABC, Quận 1, TP.HCM</p>
                                    </div>
                                </div>
                                <div className="flex items-start gap-4">
                                    <div className="p-3 bg-purple-50 rounded-xl">
                                        <Clock size={24} className="text-purple-600" />
                                    </div>
                                    <div>
                                        <p className="font-bold text-slate-900">Giờ làm việc</p>
                                        <p className="text-slate-600">T2-T7: 8:00 - 22:00</p>
                                        <p className="text-slate-600">CN: 9:00 - 21:00</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Contact Form */}
                    <div className="lg:col-span-2">
                        <div className="bg-white border-2 border-slate-100 rounded-3xl p-8 shadow-sm">
                            <h2 className="text-2xl font-bold text-slate-900 mb-6">Gửi tin nhắn cho chúng tôi</h2>
                            <form className="space-y-6">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div>
                                        <label className="block text-sm font-bold text-slate-700 mb-2">Họ và tên *</label>
                                        <input
                                            type="text"
                                            value={formData.name}
                                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                            className="w-full px-4 py-3 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                            placeholder="Nguyễn Văn A"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-bold text-slate-700 mb-2">Email *</label>
                                        <input
                                            type="email"
                                            value={formData.email}
                                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                            className="w-full px-4 py-3 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                            placeholder="email@example.com"
                                        />
                                    </div>
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-slate-700 mb-2">Chủ đề</label>
                                    <input
                                        type="text"
                                        value={formData.subject}
                                        onChange={(e) => setFormData({ ...formData, subject: e.target.value })}
                                        className="w-full px-4 py-3 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                        placeholder="Vấn đề cần hỗ trợ"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-slate-700 mb-2">Nội dung *</label>
                                    <textarea
                                        value={formData.message}
                                        onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                                        rows={6}
                                        className="w-full px-4 py-3 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                                        placeholder="Mô tả chi tiết vấn đề của bạn..."
                                    />
                                </div>
                                <button
                                    type="submit"
                                    className="w-full bg-slate-900 text-white py-4 rounded-2xl font-bold hover:bg-slate-800 transition-all shadow-xl flex items-center justify-center gap-2"
                                >
                                    <Send size={20} />
                                    Gửi tin nhắn
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
