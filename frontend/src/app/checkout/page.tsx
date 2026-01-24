'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useCartStore } from '@/store/cartStore';
import { motion } from 'framer-motion';
import { Textarea } from '@/components/ui';
import { ShoppingBag, User, MapPin, Phone, Mail, CreditCard, Truck, Check } from 'lucide-react';
import Image from 'next/image';

export default function CheckoutPage() {
    const router = useRouter();
    const { items, totalPrice, clearCart } = useCartStore();
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        // Thông tin khách hàng
        recipientName: '',
        email: '',
        phone: '',

        // Địa chỉ giao hàng
        addressLine1: '',
        addressLine2: '',
        city: '',

        // Phương thức thanh toán
        paymentMethod: 'COD',

        // Ghi chú
        notes: '',
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            // Chuẩn bị dữ liệu đơn hàng theo format backend
            const orderData = {
                customerId: 'guest-' + Date.now(), // Temporary guest ID
                customerEmail: formData.email,
                items: items.map(item => ({
                    productId: String(item.id),
                    productName: item.name,
                    productSku: item.slug || '',
                    quantity: item.quantity,
                    unitPrice: item.salePrice || item.price,
                })),
                shippingAddress: {
                    recipientName: formData.recipientName,
                    phone: formData.phone,
                    addressLine1: formData.addressLine1,
                    addressLine2: formData.addressLine2,
                    city: formData.city,
                    state: '',
                    postalCode: '',
                    country: 'VN',
                },
                paymentMethod: formData.paymentMethod,
                notes: formData.notes,
            };

            // Gọi API tạo đơn hàng
            const response = await fetch('http://localhost:8081/api/v1/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(orderData),
            });

            if (response.ok) {
                const result = await response.json();
                clearCart();
                alert('Đặt hàng thành công! Mã đơn hàng: ' + result.data.orderNumber);
                router.push('/');
            } else {
                const error = await response.json();
                alert('Có lỗi xảy ra: ' + (error.message || 'Vui lòng thử lại'));
            }
        } catch (error) {
            console.error('Error creating order:', error);
            alert('Không thể kết nối đến server. Vui lòng thử lại.');
        } finally {
            setLoading(false);
        }
    };

    if (items.length === 0) {
        return (
            <div className="section-container min-h-[70vh] flex items-center justify-center py-20">
                <div className="text-center space-y-6">
                    <ShoppingBag size={80} className="text-slate-300 mx-auto" />
                    <h2 className="text-3xl font-bold text-slate-900">Giỏ hàng trống</h2>
                    <p className="text-slate-500">Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-blue-50/30 py-12">
            <div className="section-container">
                {/* Header with better design */}
                <div className="mb-16 text-center relative">
                    <div className="absolute inset-0 -z-10 blur-3xl opacity-30">
                        <div className="absolute top-0 left-1/4 w-96 h-96 bg-blue-400 rounded-full"></div>
                        <div className="absolute top-0 right-1/4 w-96 h-96 bg-purple-400 rounded-full"></div>
                    </div>
                    <motion.div
                        initial={{ opacity: 0, y: -20 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="space-y-4"
                    >
                        <div className="inline-flex items-center gap-2 px-3 py-1.5 bg-blue-100 text-blue-700 rounded-full text-xs font-bold mb-3">
                            <ShoppingBag size={14} />
                            <span>Bước cuối cùng</span>
                        </div>
                        <h1 className="text-3xl md:text-4xl font-black text-slate-900">
                            Thanh toán
                        </h1>
                        <p className="text-base text-slate-600 max-w-2xl mx-auto">
                            Hoàn tất đơn hàng của bạn chỉ với vài bước đơn giản
                        </p>
                    </motion.div>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
                        {/* Form bên trái */}
                        <div className="lg:col-span-2 space-y-8">
                            {/* Thong tin khach hang - Enhanced */}
                            <motion.div
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                className="bg-white rounded-3xl p-10 shadow-xl border-2 border-slate-100 hover:border-blue-200 transition-all relative overflow-hidden"
                            >
                                <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-blue-100 to-purple-100 rounded-full blur-3xl opacity-20 -z-10"></div>

                                <div className="flex items-center gap-3 mb-6">
                                    <div className="p-3 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg">
                                        <User className="text-white" size={20} />
                                    </div>
                                    <div>
                                        <h2 className="text-xl font-bold text-slate-900">Thông tin khách hàng</h2>
                                        <p className="text-xs text-slate-500">Vui lòng điền đầy đủ thông tin</p>
                                    </div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div className="md:col-span-2">
                                        <label className="block text-sm font-semibold text-slate-700 mb-2">
                                            Họ và tên <span className="text-rose-500">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            name="recipientName"
                                            value={formData.recipientName}
                                            onChange={handleInputChange}
                                            required
                                            className="input-field"
                                            placeholder="Nguyễn Văn A"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-slate-700 mb-2">
                                            Email <span className="text-rose-500">*</span>
                                        </label>
                                        <input
                                            type="email"
                                            name="email"
                                            value={formData.email}
                                            onChange={handleInputChange}
                                            required
                                            className="input-field"
                                            placeholder="email@example.com"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-slate-700 mb-2">
                                            Số điện thoại <span className="text-rose-500">*</span>
                                        </label>
                                        <input
                                            type="tel"
                                            name="phone"
                                            value={formData.phone}
                                            onChange={handleInputChange}
                                            required
                                            className="input-field"
                                            placeholder="0123456789"
                                        />
                                    </div>
                                </div>
                            </motion.div>

                            {/* Dia chi giao hang - Enhanced */}
                            <motion.div
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: 0.1 }}
                                className="bg-white rounded-3xl p-10 shadow-xl border-2 border-slate-100 hover:border-emerald-200 transition-all relative overflow-hidden"
                            >
                                <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-emerald-100 to-teal-100 rounded-full blur-3xl opacity-20 -z-10"></div>

                                <div className="flex items-center gap-3 mb-6">
                                    <div className="p-3 bg-gradient-to-br from-emerald-500 to-emerald-600 rounded-xl shadow-lg">
                                        <MapPin className="text-white" size={20} />
                                    </div>
                                    <div>
                                        <h2 className="text-xl font-bold text-slate-900">Địa chỉ giao hàng</h2>
                                        <p className="text-xs text-slate-500">Nơi bạn muốn nhận hàng</p>
                                    </div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div className="md:col-span-2">
                                        <label className="block text-sm font-semibold text-slate-700 mb-2">
                                            Địa chỉ cụ thể <span className="text-rose-500">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            name="addressLine1"
                                            value={formData.addressLine1}
                                            onChange={handleInputChange}
                                            required
                                            className="input-field"
                                            placeholder="Số nhà, tên đường"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-slate-700 mb-2">
                                            Quận/Huyện <span className="text-rose-500">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            name="addressLine2"
                                            value={formData.addressLine2}
                                            onChange={handleInputChange}
                                            required
                                            className="input-field"
                                            placeholder="Quận 1, Phường Bến Nghé"
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-semibold text-slate-700 mb-2">
                                            Tỉnh/Thành phố <span className="text-rose-500">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            name="city"
                                            value={formData.city}
                                            onChange={handleInputChange}
                                            required
                                            className="input-field"
                                            placeholder="TP. Hồ Chí Minh"
                                        />
                                    </div>
                                </div>
                            </motion.div>

                            {/* Phuong thuc thanh toan - Enhanced */}
                            <motion.div
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: 0.2 }}
                                className="bg-white rounded-3xl p-10 shadow-xl border-2 border-slate-100 hover:border-amber-200 transition-all relative overflow-hidden"
                            >
                                <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-amber-100 to-orange-100 rounded-full blur-3xl opacity-20 -z-10"></div>

                                <div className="flex items-center gap-3 mb-6">
                                    <div className="p-3 bg-gradient-to-br from-amber-500 to-amber-600 rounded-xl shadow-lg">
                                        <CreditCard className="text-white" size={20} />
                                    </div>
                                    <div>
                                        <h2 className="text-xl font-bold text-slate-900">Phương thức thanh toán</h2>
                                        <p className="text-xs text-slate-500">Chọn cách bạn muốn thanh toán</p>
                                    </div>
                                </div>

                                <div className="space-y-3">
                                    <label className={`flex items-center gap-4 p-4 border-2 rounded-xl cursor-pointer transition-all ${formData.paymentMethod === 'COD' ? 'border-amber-500 bg-amber-50' : 'border-slate-200 hover:border-amber-300'}`}>
                                        <input
                                            type="radio"
                                            name="paymentMethod"
                                            value="COD"
                                            checked={formData.paymentMethod === 'COD'}
                                            onChange={handleInputChange}
                                            className="w-5 h-5 text-amber-600"
                                        />
                                        <div className="p-2 bg-white rounded-lg">
                                            <Truck size={20} className="text-amber-600" />
                                        </div>
                                        <div className="flex-1">
                                            <p className="font-bold text-slate-900">Thanh toán khi nhận hàng (COD)</p>
                                            <p className="text-xs text-slate-500 mt-0.5">Thanh toán bằng tiền mặt khi nhận hàng</p>
                                        </div>
                                        {formData.paymentMethod === 'COD' && (
                                            <Check size={20} className="text-amber-600" />
                                        )}
                                    </label>

                                    <label className={`flex items-center gap-4 p-4 border-2 rounded-xl cursor-pointer transition-all ${formData.paymentMethod === 'BANK_TRANSFER' ? 'border-amber-500 bg-amber-50' : 'border-slate-200 hover:border-amber-300'}`}>
                                        <input
                                            type="radio"
                                            name="paymentMethod"
                                            value="BANK_TRANSFER"
                                            checked={formData.paymentMethod === 'BANK_TRANSFER'}
                                            onChange={handleInputChange}
                                            className="w-5 h-5 text-amber-600"
                                        />
                                        <div className="p-2 bg-white rounded-lg">
                                            <CreditCard size={20} className="text-amber-600" />
                                        </div>
                                        <div className="flex-1">
                                            <p className="font-bold text-slate-900">Chuyển khoản ngân hàng</p>
                                            <p className="text-xs text-slate-500 mt-0.5">Chuyển khoản qua Internet Banking</p>
                                        </div>
                                        {formData.paymentMethod === 'BANK_TRANSFER' && (
                                            <Check size={20} className="text-amber-600" />
                                        )}
                                    </label>
                                </div>
                            </motion.div>

                            {/* Ghi chú */}
                            <motion.div
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: 0.3 }}
                                className="bg-white rounded-3xl p-8 shadow-lg border border-slate-100"
                            >
                                <Textarea
                                    label="Ghi chú đơn hàng (tùy chọn)"
                                    name="notes"
                                    value={formData.notes}
                                    onChange={handleInputChange}
                                    rows={3}
                                    placeholder="Ghi chú về đơn hàng, ví dụ: thời gian giao hàng..."
                                />
                            </motion.div>
                        </div>

                        {/* Tóm tắt đơn hàng bên phải */}
                        <div className="lg:col-span-1">
                            <motion.div
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: 0.4 }}
                                className="bg-white rounded-3xl p-8 shadow-2xl sticky top-24 border border-slate-100"
                            >
                                <h3 className="text-xl font-bold text-slate-900 mb-4">Đơn hàng của bạn</h3>

                                {/* Danh sách sản phẩm */}
                                <div className="space-y-4 mb-6 max-h-64 overflow-y-auto">
                                    {items.map((item) => (
                                        <div key={item.id} className="flex gap-4">
                                            <div className="relative w-20 h-20 bg-slate-50 rounded-xl overflow-hidden flex-shrink-0">
                                                <Image
                                                    src={item.thumbnail || '/placeholder.png'}
                                                    alt={item.name}
                                                    fill
                                                    className="object-cover"
                                                />
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <p className="font-bold text-sm text-slate-900 line-clamp-2">{item.name}</p>
                                                <p className="text-sm text-slate-500">x{item.quantity}</p>
                                                <p className="text-sm font-bold text-slate-900">
                                                    {((item.salePrice || item.price) * item.quantity).toLocaleString('vi-VN')}đ
                                                </p>
                                            </div>
                                        </div>
                                    ))}
                                </div>

                                {/* Tổng tiền */}
                                <div className="space-y-3 py-6 border-y-2 border-slate-100">
                                    <div className="flex justify-between text-slate-600">
                                        <span>Tạm tính</span>
                                        <span className="font-bold">{totalPrice().toLocaleString('vi-VN')}đ</span>
                                    </div>
                                    <div className="flex justify-between text-slate-600">
                                        <span>Phí vận chuyển</span>
                                        <span className="font-bold text-emerald-600">Miễn phí</span>
                                    </div>
                                </div>

                                <div className="flex justify-between items-baseline pt-6 mb-8">
                                    <span className="font-bold text-slate-900">Tổng cộng</span>
                                    <span className="text-2xl font-black text-slate-900">{totalPrice().toLocaleString('vi-VN')}đ</span>
                                </div>

                                {/* Submit button */}
                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="w-full bg-gradient-to-r from-slate-900 to-slate-800 text-white py-5 rounded-2xl font-bold text-center hover:from-slate-800 hover:to-slate-700 transition-all shadow-2xl hover:shadow-3xl text-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-3"
                                >
                                    {loading ? (
                                        <>
                                            <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                                            Đang xử lý...
                                        </>
                                    ) : (
                                        <>
                                            <Check size={24} />
                                            Đặt hàng
                                        </>
                                    )}
                                </button>

                                <p className="text-xs text-slate-500 text-center mt-4">
                                    Bằng cách đặt hàng, bạn đồng ý với điều khoản sử dụng của chúng tôi
                                </p>
                            </motion.div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}
