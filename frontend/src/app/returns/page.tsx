'use client';

import { RotateCcw, Package, CheckCircle, XCircle, AlertCircle } from 'lucide-react';

export default function ReturnsPage() {
    return (
        <div className="bg-white min-h-screen">
            <section className="bg-gradient-to-br from-rose-50 to-pink-50 py-20">
                <div className="section-container text-center space-y-6">
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">Chính sách đổi trả</h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Quy trình đổi trả đơn giản và thuận tiện cho khách hàng
                    </p>
                </div>
            </section>

            <div className="section-container py-20 max-w-4xl mx-auto space-y-16">
                {/* Return Conditions */}
                <section className="space-y-8">
                    <h2 className="text-3xl font-bold text-slate-900">Điều kiện đổi trả</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="bg-emerald-50 border-2 border-emerald-100 rounded-3xl p-8">
                            <CheckCircle size={32} className="text-emerald-600 mb-4" />
                            <h3 className="text-xl font-bold text-slate-900 mb-4">Được chấp nhận</h3>
                            <ul className="space-y-2 text-slate-700">
                                <li>• Sản phẩm còn nguyên vẹn, chưa qua sử dụng</li>
                                <li>• Còn đầy đủ hộp, phụ kiện, tem mác</li>
                                <li>• Trong vòng 30 ngày kể từ ngày mua</li>
                                <li>• Có hóa đơn mua hàng</li>
                            </ul>
                        </div>
                        <div className="bg-rose-50 border-2 border-rose-100 rounded-3xl p-8">
                            <XCircle size={32} className="text-rose-600 mb-4" />
                            <h3 className="text-xl font-bold text-slate-900 mb-4">Không chấp nhận</h3>
                            <ul className="space-y-2 text-slate-700">
                                <li>• Sản phẩm đã qua sử dụng</li>
                                <li>• Thiếu phụ kiện, hộp đựng</li>
                                <li>• Quá 30 ngày kể từ ngày mua</li>
                                <li>• Sản phẩm khuyến mãi, giảm giá sâu</li>
                            </ul>
                        </div>
                    </div>
                </section>

                {/* Return Process */}
                <section className="space-y-8">
                    <h2 className="text-3xl font-bold text-slate-900">Quy trình đổi trả</h2>
                    <div className="space-y-4">
                        {[
                            { step: 1, title: 'Liên hệ hỗ trợ', desc: 'Gọi hotline 1900 xxxx hoặc gửi yêu cầu qua website' },
                            { step: 2, title: 'Xác nhận đơn hàng', desc: 'Cung cấp mã đơn hàng và lý do đổi trả' },
                            { step: 3, title: 'Gửi sản phẩm', desc: 'Đóng gói sản phẩm và gửi về địa chỉ của LUXORA' },
                            { step: 4, title: 'Kiểm tra sản phẩm', desc: 'Chúng tôi sẽ kiểm tra sản phẩm trong vòng 2-3 ngày' },
                            { step: 5, title: 'Hoàn tiền/Đổi hàng', desc: 'Hoàn tiền trong 5-7 ngày hoặc gửi sản phẩm mới' },
                        ].map((item) => (
                            <div key={item.step} className="flex gap-6 bg-slate-50 rounded-2xl p-6">
                                <div className="flex-shrink-0 w-12 h-12 bg-blue-600 text-white rounded-full flex items-center justify-center font-bold text-lg">
                                    {item.step}
                                </div>
                                <div>
                                    <h3 className="font-bold text-lg text-slate-900 mb-1">{item.title}</h3>
                                    <p className="text-slate-600">{item.desc}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                {/* Refund Policy */}
                <section className="space-y-6">
                    <h2 className="text-3xl font-bold text-slate-900">Chính sách hoàn tiền</h2>
                    <div className="bg-blue-50 rounded-3xl p-8 space-y-4">
                        <div className="flex items-start gap-4">
                            <AlertCircle size={24} className="text-blue-600 mt-1" />
                            <div>
                                <p className="font-bold text-slate-900 mb-2">Thời gian hoàn tiền</p>
                                <p className="text-slate-700">5-7 ngày làm việc kể từ khi sản phẩm được xác nhận đủ điều kiện đổi trả</p>
                            </div>
                        </div>
                        <div className="flex items-start gap-4">
                            <AlertCircle size={24} className="text-blue-600 mt-1" />
                            <div>
                                <p className="font-bold text-slate-900 mb-2">Phương thức hoàn tiền</p>
                                <p className="text-slate-700">Hoàn tiền qua tài khoản ngân hàng hoặc ví điện tử đã thanh toán</p>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    );
}
