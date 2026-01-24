'use client';

import { Ruler, User } from 'lucide-react';

export default function SizeGuidePage() {
    return (
        <div className="bg-white min-h-screen">
            <section className="bg-gradient-to-br from-amber-50 to-orange-50 py-20">
                <div className="section-container text-center space-y-6">
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">Hướng dẫn chọn size</h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Tìm size phù hợp nhất cho bạn
                    </p>
                </div>
            </section>

            <div className="section-container py-20 max-w-6xl mx-auto space-y-16">
                {/* How to Measure */}
                <section className="space-y-8">
                    <h2 className="text-3xl font-bold text-slate-900">Cách đo size</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        <div className="bg-slate-50 rounded-3xl p-8">
                            <Ruler size={32} className="text-blue-600 mb-4" />
                            <h3 className="text-xl font-bold text-slate-900 mb-4">Đo chiều dài bàn chân</h3>
                            <ol className="space-y-2 text-slate-700">
                                <li>1. Đặt một tờ giấy trên sàn nhà</li>
                                <li>2. Đứng trên tờ giấy, để gót chân sát tường</li>
                                <li>3. Đánh dấu điểm xa nhất của ngón chân</li>
                                <li>4. Đo khoảng cách từ tường đến điểm đánh dấu</li>
                            </ol>
                        </div>
                        <div className="bg-slate-50 rounded-3xl p-8">
                            <User size={32} className="text-emerald-600 mb-4" />
                            <h3 className="text-xl font-bold text-slate-900 mb-4">Lưu ý khi đo</h3>
                            <ul className="space-y-2 text-slate-700">
                                <li>• Đo vào buổi chiều (chân thường to hơn)</li>
                                <li>• Đo cả hai bàn chân và chọn size lớn hơn</li>
                                <li>• Mang tất khi đo nếu dự định mang tất</li>
                                <li>• Nếu giữa 2 size, chọn size lớn hơn</li>
                            </ul>
                        </div>
                    </div>
                </section>

                {/* Size Chart */}
                <section className="space-y-8">
                    <h2 className="text-3xl font-bold text-slate-900">Bảng size giày</h2>
                    <div className="overflow-x-auto">
                        <table className="w-full bg-white border-2 border-slate-100 rounded-2xl overflow-hidden">
                            <thead className="bg-slate-900 text-white">
                                <tr>
                                    <th className="px-6 py-4 text-left font-bold">Size VN</th>
                                    <th className="px-6 py-4 text-left font-bold">Size US</th>
                                    <th className="px-6 py-4 text-left font-bold">Size EU</th>
                                    <th className="px-6 py-4 text-left font-bold">Chiều dài (cm)</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                                {[
                                    { vn: '36', us: '5', eu: '36', cm: '22.5' },
                                    { vn: '37', us: '6', eu: '37', cm: '23.0' },
                                    { vn: '38', us: '7', eu: '38', cm: '24.0' },
                                    { vn: '39', us: '8', eu: '39', cm: '24.5' },
                                    { vn: '40', us: '9', eu: '40', cm: '25.0' },
                                    { vn: '41', us: '10', eu: '41', cm: '26.0' },
                                    { vn: '42', us: '11', eu: '42', cm: '26.5' },
                                    { vn: '43', us: '12', eu: '43', cm: '27.0' },
                                ].map((size, idx) => (
                                    <tr key={idx} className="hover:bg-slate-50 transition-colors">
                                        <td className="px-6 py-4 font-bold text-slate-900">{size.vn}</td>
                                        <td className="px-6 py-4 text-slate-700">{size.us}</td>
                                        <td className="px-6 py-4 text-slate-700">{size.eu}</td>
                                        <td className="px-6 py-4 text-slate-700">{size.cm}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </section>

                {/* Contact CTA */}
                <section className="bg-gradient-to-br from-blue-600 to-purple-600 rounded-[48px] p-12 text-center text-white">
                    <h2 className="text-3xl font-bold mb-4">Vẫn chưa chắc chắn về size?</h2>
                    <p className="text-lg text-blue-100 mb-8 max-w-2xl mx-auto">
                        Đội ngũ tư vấn của chúng tôi sẵn sàng giúp bạn chọn size phù hợp nhất
                    </p>
                    <a
                        href="/contact"
                        className="inline-block px-8 py-4 bg-white text-blue-600 rounded-2xl font-bold hover:bg-blue-50 transition-all"
                    >
                        Liên hệ tư vấn
                    </a>
                </section>
            </div>
        </div>
    );
}
