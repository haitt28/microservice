'use client';

export default function PrivacyPage() {
    return (
        <div className="bg-white min-h-screen">
            <section className="bg-gradient-to-br from-purple-50 to-pink-50 py-20">
                <div className="section-container text-center space-y-6">
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">Chính sách bảo mật</h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Cam kết bảo vệ thông tin cá nhân của khách hàng
                    </p>
                </div>
            </section>

            <div className="section-container py-20 max-w-4xl mx-auto prose prose-lg prose-slate">
                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">1. Thu thập thông tin</h2>
                    <p className="text-slate-700 leading-relaxed">
                        LUXORA thu thập thông tin cá nhân khi bạn đăng ký tài khoản, đặt hàng, hoặc tương tác với dịch vụ của chúng tôi.
                        Thông tin có thể bao gồm: họ tên, địa chỉ email, số điện thoại, địa chỉ giao hàng, và thông tin thanh toán.
                    </p>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">2. Sử dụng thông tin</h2>
                    <p className="text-slate-700 leading-relaxed">
                        Chúng tôi sử dụng thông tin của bạn để:
                    </p>
                    <ul className="list-disc pl-6 space-y-2 text-slate-700">
                        <li>Xử lý và giao hàng đơn đặt hàng</li>
                        <li>Cung cấp dịch vụ khách hàng</li>
                        <li>Gửi thông tin về sản phẩm và khuyến mãi (nếu bạn đồng ý)</li>
                        <li>Cải thiện trải nghiệm mua sắm</li>
                        <li>Phân tích và nghiên cứu thị trường</li>
                    </ul>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">3. Bảo vệ thông tin</h2>
                    <p className="text-slate-700 leading-relaxed">
                        LUXORA cam kết bảo vệ thông tin cá nhân của bạn bằng các biện pháp bảo mật tiên tiến, bao gồm mã hóa SSL,
                        tường lửa, và kiểm soát truy cập nghiêm ngặt. Chúng tôi không bán hoặc chia sẻ thông tin của bạn với bên thứ ba
                        mà không có sự đồng ý của bạn.
                    </p>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">4. Cookies</h2>
                    <p className="text-slate-700 leading-relaxed">
                        Website của chúng tôi sử dụng cookies để cải thiện trải nghiệm người dùng. Cookies giúp chúng tôi nhớ tùy chọn của bạn
                        và cung cấp nội dung phù hợp. Bạn có thể tắt cookies trong cài đặt trình duyệt, nhưng điều này có thể ảnh hưởng đến
                        một số tính năng của website.
                    </p>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">5. Quyền của bạn</h2>
                    <p className="text-slate-700 leading-relaxed">
                        Bạn có quyền:
                    </p>
                    <ul className="list-disc pl-6 space-y-2 text-slate-700">
                        <li>Truy cập và xem thông tin cá nhân của bạn</li>
                        <li>Yêu cầu chỉnh sửa hoặc xóa thông tin</li>
                        <li>Từ chối nhận email marketing</li>
                        <li>Yêu cầu xuất dữ liệu cá nhân</li>
                    </ul>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">6. Liên hệ</h2>
                    <p className="text-slate-700 leading-relaxed">
                        Nếu bạn có bất kỳ câu hỏi nào về chính sách bảo mật này, vui lòng liên hệ với chúng tôi qua email:
                        <strong> privacy@luxora.vn</strong>
                    </p>
                </section>

                <section className="bg-blue-50 rounded-3xl p-8 mt-12">
                    <p className="text-sm text-slate-600">
                        <strong>Tuân thủ GDPR:</strong> LUXORA tuân thủ các quy định về bảo vệ dữ liệu cá nhân theo GDPR và các luật bảo mật hiện hành.<br />
                        <strong>Cập nhật lần cuối:</strong> 23/01/2024
                    </p>
                </section>
            </div>
        </div>
    );
}
