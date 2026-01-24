'use client';

import { Truck, Package, MapPin, Clock, CheckCircle } from 'lucide-react';

export default function ShippingPage() {
    return (
        <div className="bg-white min-h-screen">
            <section className="bg-gradient-to-br from-emerald-50 to-teal-50 py-20">
                <div className="section-container text-center space-y-6">
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">Chính sách vận chuyển</h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Thông tin chi tiết về phương thức và thời gian giao hàng
                    </p>
                </div>
            </section>

            <div className="section-container py-20 max-w-4xl mx-auto space-y-16">
                {/* Shipping Methods */}
                <section className="space-y-8">
                    <h2 className="text-3xl font-bold text-slate-900">Phương thức vận chuyển</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="bg-white border-2 border-slate-100 rounded-3xl p-8 space-y-4">
                            <div className="inline-flex p-4 bg-blue-50 rounded-2xl">
                                <Truck size={32} className="text-blue-600" />
                            </div>
                            <h3 className="text-xl font-bold text-slate-900">Giao hàng nhanh 2h</h3>
                            <p className="text-slate-600">Áp dụng cho khu vực nội thành TP.HCM và Hà Nội</p>
                            <p className="text-2xl font-bold text-blue-600">MIỄN PHÍ</p>
                        </div>
                        <div className="bg-white border-2 border-slate-100 rounded-3xl p-8 space-y-4">
                            <div className="inline-flex p-4 bg-emerald-50 rounded-2xl">
                                <Package size={32} className="text-emerald-600" />
                            </div>
                            <h3 className="text-xl font-bold text-slate-900">Giao hàng tiêu chuẩn</h3>
                            <p className="text-slate-600">2-5 ngày làm việc cho toàn quốc</p>
                            <p className="text-2xl font-bold text-emerald-600">30.000đ</p>
                        </div>
                    </div>
                </section>

                {/* Delivery Time */}
                <section className="space-y-6">
                    <h2 className="text-3xl font-bold text-slate-900">Thời gian giao hàng</h2>
                    <div className="bg-slate-50 rounded-3xl p-8 space-y-4">
                        <div className="flex items-start gap-4">
                            <Clock size={24} className="text-blue-600 mt-1" />
                            <div>
                                <p className="font-bold text-slate-900 mb-2">Nội thành TP.HCM & Hà Nội</p>
                                <p className="text-slate-600">1-2 ngày làm việc</p>
                            </div>
                        </div>
                        <div className="flex items-start gap-4">
                            <Clock size={24} className="text-emerald-600 mt-1" />
                            <div>
                                <p className="font-bold text-slate-900 mb-2">Các tỉnh thành khác</p>
                                <p className="text-slate-600">3-5 ngày làm việc</p>
                            </div>
                        </div>
                        <div className="flex items-start gap-4">
                            <Clock size={24} className="text-amber-600 mt-1" />
                            <div>
                                <p className="font-bold text-slate-900 mb-2">Vùng xa, hải đảo</p>
                                <p className="text-slate-600">5-7 ngày làm việc</p>
                            </div>
                        </div>
                    </div>
                </section>

                {/* Free Shipping */}
                <section className="space-y-6">
                    <h2 className="text-3xl font-bold text-slate-900">Miễn phí vận chuyển</h2>
                    <div className="bg-gradient-to-br from-blue-50 to-purple-50 rounded-3xl p-8">
                        <div className="flex items-start gap-4 mb-4">
                            <CheckCircle size={24} className="text-blue-600 mt-1" />
                            <p className="text-lg text-slate-900">Đơn hàng từ <span className="font-bold">500.000đ</span> trở lên</p>
                        </div>
                        <div className="flex items-start gap-4">
                            <CheckCircle size={24} className="text-blue-600 mt-1" />
                            <p className="text-lg text-slate-900">Khách hàng thành viên VIP</p>
                        </div>
                    </div>
                </section>

                {/* Tracking */}
                <section className="space-y-6">
                    <h2 className="text-3xl font-bold text-slate-900">Theo dõi đơn hàng</h2>
                    <p className="text-lg text-slate-600 leading-relaxed">
                        Sau khi đơn hàng được giao cho đơn vị vận chuyển, bạn sẽ nhận được mã vận đơn qua email và SMS.
                        Bạn có thể theo dõi tình trạng đơn hàng trong mục "Đơn hàng của tôi" hoặc liên hệ hotline
                        <span className="font-bold text-blue-600"> 1900 xxxx</span> để được hỗ trợ.
                    </p>
                </section>
            </div>
        </div>
    );
}
