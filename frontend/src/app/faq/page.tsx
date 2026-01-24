'use client';

import { ChevronDown, Search } from 'lucide-react';
import { motion } from 'framer-motion';
import { useState } from 'react';

const FAQ_DATA = [
    {
        category: 'Đặt hàng & Thanh toán',
        questions: [
            { q: 'Làm thế nào để đặt hàng?', a: 'Bạn có thể đặt hàng trực tiếp trên website bằng cách thêm sản phẩm vào giỏ hàng và tiến hành thanh toán.' },
            { q: 'Những phương thức thanh toán nào được chấp nhận?', a: 'Chúng tôi chấp nhận thanh toán qua thẻ tín dụng, chuyển khoản ngân hàng, ví điện tử và COD.' },
            { q: 'Tôi có thể hủy đơn hàng không?', a: 'Bạn có thể hủy đơn hàng trong vòng 24h sau khi đặt nếu đơn hàng chưa được xử lý.' },
        ]
    },
    {
        category: 'Vận chuyển',
        questions: [
            { q: 'Thời gian giao hàng là bao lâu?', a: 'Thời gian giao hàng tiêu chuẩn là 2-3 ngày làm việc cho nội thành và 3-5 ngày cho tỉnh thành.' },
            { q: 'Phí vận chuyển là bao nhiêu?', a: 'Miễn phí vận chuyển cho đơn hàng trên 500.000đ. Dưới 500.000đ phí vận chuyển là 30.000đ.' },
            { q: 'Làm thế nào để theo dõi đơn hàng?', a: 'Bạn có thể theo dõi đơn hàng trong mục "Đơn hàng của tôi" sau khi đăng nhập.' },
        ]
    },
    {
        category: 'Đổi trả & Hoàn tiền',
        questions: [
            { q: 'Chính sách đổi trả như thế nào?', a: 'Chúng tôi chấp nhận đổi trả trong vòng 30 ngày nếu sản phẩm còn nguyên vẹn và chưa qua sử dụng.' },
            { q: 'Thời gian hoàn tiền là bao lâu?', a: 'Thời gian hoàn tiền là 5-7 ngày làm việc kể từ khi chúng tôi nhận được sản phẩm trả lại.' },
        ]
    },
    {
        category: 'Sản phẩm',
        questions: [
            { q: 'Sản phẩm có chính hãng không?', a: 'Tất cả sản phẩm tại LUXORA đều là hàng chính hãng 100% với đầy đủ tem bảo hành.' },
            { q: 'Bảo hành sản phẩm như thế nào?', a: 'Sản phẩm được bảo hành 12-24 tháng tùy theo từng loại sản phẩm, theo chính sách của nhà sản xuất.' },
        ]
    },
];

export default function FAQPage() {
    const [openIndex, setOpenIndex] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState('');

    return (
        <div className="bg-white min-h-screen">
            {/* Hero */}
            <section className="bg-gradient-to-br from-purple-50 to-blue-50 py-20">
                <div className="section-container text-center space-y-8">
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">Câu hỏi thường gặp</h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Tìm câu trả lời cho những thắc mắc phổ biến của bạn
                    </p>
                    {/* Search */}
                    <div className="max-w-2xl mx-auto relative">
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            placeholder="Tìm kiếm câu hỏi..."
                            className="w-full px-6 py-4 pr-14 border-2 border-slate-200 rounded-2xl focus:ring-2 focus:ring-blue-500 focus:border-transparent text-lg"
                        />
                        <Search className="absolute right-5 top-1/2 -translate-y-1/2 text-slate-400" size={24} />
                    </div>
                </div>
            </section>

            {/* FAQ Accordion */}
            <section className="section-container py-20">
                <div className="max-w-4xl mx-auto space-y-12">
                    {FAQ_DATA.map((category, catIdx) => (
                        <div key={catIdx} className="space-y-6">
                            <h2 className="text-2xl font-bold text-slate-900 pb-4 border-b-2 border-slate-100">
                                {category.category}
                            </h2>
                            <div className="space-y-4">
                                {category.questions.map((faq, qIdx) => {
                                    const key = `${catIdx}-${qIdx}`;
                                    const isOpen = openIndex === key;
                                    return (
                                        <motion.div
                                            key={key}
                                            initial={false}
                                            className="bg-white border-2 border-slate-100 rounded-2xl overflow-hidden hover:border-slate-200 transition-all"
                                        >
                                            <button
                                                onClick={() => setOpenIndex(isOpen ? null : key)}
                                                className="w-full px-6 py-5 flex items-center justify-between text-left"
                                            >
                                                <span className="font-bold text-lg text-slate-900 pr-4">{faq.q}</span>
                                                <ChevronDown
                                                    size={24}
                                                    className={`text-slate-400 transition-transform flex-shrink-0 ${isOpen ? 'rotate-180' : ''}`}
                                                />
                                            </button>
                                            <motion.div
                                                initial={false}
                                                animate={{ height: isOpen ? 'auto' : 0 }}
                                                className="overflow-hidden"
                                            >
                                                <div className="px-6 pb-5 text-slate-600 leading-relaxed">
                                                    {faq.a}
                                                </div>
                                            </motion.div>
                                        </motion.div>
                                    );
                                })}
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            {/* CTA */}
            <section className="section-container py-20">
                <div className="bg-slate-900 rounded-[48px] p-16 text-center text-white">
                    <h2 className="text-3xl font-bold mb-4">Vẫn còn thắc mắc?</h2>
                    <p className="text-slate-300 mb-8">Đội ngũ hỗ trợ của chúng tôi luôn sẵn sàng giúp đỡ bạn</p>
                    <a
                        href="/contact"
                        className="inline-block px-8 py-4 bg-white text-slate-900 rounded-2xl font-bold hover:bg-slate-100 transition-all"
                    >
                        Liên hệ hỗ trợ
                    </a>
                </div>
            </section>
        </div>
    );
}
