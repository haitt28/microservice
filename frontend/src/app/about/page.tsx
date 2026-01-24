'use client';

import { Target, Users, Award, TrendingUp, Heart, Shield } from 'lucide-react';
import { motion } from 'framer-motion';
import Image from 'next/image';

export default function AboutPage() {
    return (
        <div className="bg-white min-h-screen">
            {/* Hero */}
            <section className="bg-gradient-to-br from-slate-900 to-blue-900 text-white py-32">
                <div className="section-container text-center space-y-8">
                    <motion.h1
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="text-5xl md:text-7xl font-bold"
                    >
                        Về LUXORA
                    </motion.h1>
                    <p className="text-xl text-slate-300 max-w-3xl mx-auto leading-relaxed">
                        Chúng tôi tin rằng công nghệ và thiết kế có thể thay đổi cuộc sống.
                        LUXORA ra đời với sứ mệnh mang đến những sản phẩm cao cấp nhất cho người Việt.
                    </p>
                </div>
            </section>

            {/* Mission & Vision */}
            <section className="section-container py-20">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
                    <div className="space-y-6">
                        <div className="inline-flex p-4 bg-blue-50 rounded-2xl">
                            <Target size={32} className="text-blue-600" />
                        </div>
                        <h2 className="text-3xl font-bold text-slate-900">Sứ mệnh</h2>
                        <p className="text-lg text-slate-600 leading-relaxed">
                            Nâng tầm trải nghiệm mua sắm công nghệ tại Việt Nam thông qua việc tuyển chọn
                            những sản phẩm chất lượng cao nhất và dịch vụ khách hàng xuất sắc.
                        </p>
                    </div>
                    <div className="space-y-6">
                        <div className="inline-flex p-4 bg-purple-50 rounded-2xl">
                            <TrendingUp size={32} className="text-purple-600" />
                        </div>
                        <h2 className="text-3xl font-bold text-slate-900">Tầm nhìn</h2>
                        <p className="text-lg text-slate-600 leading-relaxed">
                            Trở thành nền tảng thương mại điện tử hàng đầu Việt Nam về sản phẩm công nghệ
                            cao cấp, được khách hàng tin tưởng và yêu mến.
                        </p>
                    </div>
                </div>
            </section>

            {/* Values */}
            <section className="bg-slate-50 py-20">
                <div className="section-container">
                    <h2 className="text-4xl font-bold text-slate-900 text-center mb-16">Giá trị cốt lõi</h2>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        {[
                            { icon: Heart, title: 'Tận tâm', desc: 'Đặt khách hàng làm trung tâm trong mọi quyết định', color: 'rose' },
                            { icon: Shield, title: 'Uy tín', desc: 'Cam kết chất lượng và minh bạch trong kinh doanh', color: 'blue' },
                            { icon: Award, title: 'Xuất sắc', desc: 'Không ngừng cải tiến để mang đến trải nghiệm tốt nhất', color: 'amber' },
                        ].map((value, idx) => (
                            <motion.div
                                key={idx}
                                initial={{ opacity: 0, y: 20 }}
                                whileInView={{ opacity: 1, y: 0 }}
                                transition={{ delay: idx * 0.1 }}
                                viewport={{ once: true }}
                                className="bg-white rounded-3xl p-8 text-center space-y-4 hover:shadow-xl transition-all"
                            >
                                <div className={`inline-flex p-6 bg-${value.color}-50 rounded-2xl`}>
                                    <value.icon size={40} className={`text-${value.color}-600`} />
                                </div>
                                <h3 className="text-2xl font-bold text-slate-900">{value.title}</h3>
                                <p className="text-slate-600">{value.desc}</p>
                            </motion.div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Stats */}
            <section className="section-container py-20">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
                    {[
                        { value: '50K+', label: 'Khách hàng hài lòng' },
                        { value: '1000+', label: 'Sản phẩm chất lượng' },
                        { value: '4.9/5', label: 'Đánh giá trung bình' },
                        { value: '24/7', label: 'Hỗ trợ khách hàng' },
                    ].map((stat, idx) => (
                        <div key={idx} className="text-center space-y-2">
                            <p className="text-5xl font-bold text-slate-900">{stat.value}</p>
                            <p className="text-slate-600">{stat.label}</p>
                        </div>
                    ))}
                </div>
            </section>
        </div>
    );
}
