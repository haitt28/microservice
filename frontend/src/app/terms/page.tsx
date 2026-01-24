'use client';

export default function TermsPage() {
    return (
        <div className="bg-white min-h-screen">
            <section className="bg-gradient-to-br from-slate-100 to-blue-50 py-20">
                <div className="section-container text-center space-y-6">
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900">Điều khoản dịch vụ</h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Các điều khoản và điều kiện sử dụng dịch vụ của LUXORA
                    </p>
                </div>
            </section>

            <div className="section-container py-20 max-w-4xl mx-auto prose prose-lg prose-slate">
                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">1. Chấp nhận điều khoản</h2>
                    <p className="text-slate-700 leading-relaxed">
                        Bằng việc truy cập và sử dụng website LUXORA, bạn đồng ý tuân thủ và bị ràng buộc bởi các điều khoản và điều kiện sau đây.
                        Nếu bạn không đồng ý với bất kỳ phần nào của các điều khoản này, vui lòng không sử dụng dịch vụ của chúng tôi.
                    </p>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">2. Tài khoản người dùng</h2>
                    <p className="text-slate-700 leading-relaxed">
                        Khi tạo tài khoản trên LUXORA, bạn phải cung cấp thông tin chính xác và đầy đủ. Bạn có trách nhiệm bảo mật thông tin
                        đăng nhập và chịu trách nhiệm về mọi hoạt động diễn ra dưới tài khoản của mình.
                    </p>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">3. Sử dụng dịch vụ</h2>
                    <p className="text-slate-700 leading-relaxed">
                        Bạn đồng ý sử dụng dịch vụ của LUXORA chỉ cho mục đích hợp pháp và theo cách không vi phạm quyền của bất kỳ bên thứ ba nào.
                        Nghiêm cấm các hành vi gian lận, spam, hoặc bất kỳ hoạt động nào có thể gây hại cho hệ thống.
                    </p>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">4. Sở hữu trí tuệ</h2>
                    <p className="text-slate-700 leading-relaxed">
                        Tất cả nội dung trên website LUXORA, bao gồm văn bản, hình ảnh, logo, và thiết kế đều thuộc quyền sở hữu của LUXORA
                        hoặc các đối tác được cấp phép. Nghiêm cấm sao chép, phân phối hoặc sử dụng mà không có sự cho phép.
                    </p>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">5. Giới hạn trách nhiệm</h2>
                    <p className="text-slate-700 leading-relaxed">
                        LUXORA không chịu trách nhiệm về bất kỳ thiệt hại trực tiếp, gián tiếp, ngẫu nhiên hoặc hậu quả phát sinh từ việc
                        sử dụng hoặc không thể sử dụng dịch vụ của chúng tôi.
                    </p>
                </section>

                <section className="space-y-6 mb-12">
                    <h2 className="text-3xl font-bold text-slate-900">6. Thay đổi điều khoản</h2>
                    <p className="text-slate-700 leading-relaxed">
                        LUXORA có quyền thay đổi các điều khoản này bất kỳ lúc nào. Các thay đổi sẽ có hiệu lực ngay khi được đăng tải trên website.
                        Việc bạn tiếp tục sử dụng dịch vụ sau khi có thay đổi đồng nghĩa với việc bạn chấp nhận các điều khoản mới.
                    </p>
                </section>

                <section className="bg-slate-50 rounded-3xl p-8 mt-12">
                    <p className="text-sm text-slate-600">
                        <strong>Cập nhật lần cuối:</strong> 23/01/2024<br />
                        <strong>Liên hệ:</strong> legal@luxora.vn
                    </p>
                </section>
            </div>
        </div>
    );
}
