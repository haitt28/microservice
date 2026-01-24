'use client';

import {
    ShoppingBag, Search, Eye,
    Truck, CheckCircle, XCircle, Clock
} from 'lucide-react';

const ORDERS = [
    { id: '1001', customer: 'Nguyễn Văn An', date: '2024-01-20', total: 4500000, status: 'PROCESSING' },
    { id: '1002', customer: 'Trần Thị Bình', date: '2024-01-21', total: 1200000, status: 'SHIPPED' },
    { id: '1003', customer: 'Lê Văn Cường', date: '2024-01-21', total: 850000, status: 'PENDING' },
    { id: '1004', customer: 'Phạm Minh Đức', date: '2024-01-22', total: 3200000, status: 'COMPLETED' },
];

const STATUS_MAP: any = {
    PENDING: { label: 'Chờ xác nhận', color: 'amber', icon: Clock },
    PROCESSING: { label: 'Đang xử lý', color: 'blue', icon: ShoppingBag },
    SHIPPED: { label: 'Đang giao', color: 'purple', icon: Truck },
    COMPLETED: { label: 'Hoàn thành', color: 'emerald', icon: CheckCircle },
    CANCELLED: { label: 'Đã hủy', color: 'red', icon: XCircle },
};

export default function AdminOrdersPage() {
    return (
        <div className="space-y-8">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-black text-slate-800 tracking-tight">Đơn hàng</h1>
                <div className="flex gap-2">
                    <button className="px-4 py-2 bg-white border border-slate-200 rounded-xl text-sm font-bold text-slate-600 hover:bg-slate-50 transition-colors">Lọc trạng thái</button>
                    <button className="btn-primary py-2 px-6 text-sm">Xuất Excel</button>
                </div>
            </div>

            <div className="glass-card overflow-hidden">
                <div className="p-6 border-b border-slate-100 flex gap-4">
                    <div className="relative flex-1">
                        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                        <input
                            type="text"
                            placeholder="Tìm theo Mã đơn hoặc Tên khách hàng..."
                            className="w-full bg-slate-50 border-none rounded-xl px-12 py-3 text-sm focus:ring-1 focus:ring-accent"
                        />
                    </div>
                </div>

                <table className="w-full text-left">
                    <thead className="bg-slate-50/50 text-slate-500 uppercase text-[10px] font-black tracking-widest">
                        <tr>
                            <th className="px-8 py-4">Mã đơn</th>
                            <th className="px-8 py-4">Khách hàng</th>
                            <th className="px-8 py-4">Ngày đặt</th>
                            <th className="px-8 py-4">Tổng tiền</th>
                            <th className="px-8 py-4">Trạng thái</th>
                            <th className="px-8 py-4 text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {ORDERS.map((order) => {
                            const status = STATUS_MAP[order.status];
                            const StatusIcon = status.icon;
                            return (
                                <tr key={order.id} className="group hover:bg-slate-50 transition-colors">
                                    <td className="px-8 py-5 font-bold text-slate-800">#ORD-{order.id}</td>
                                    <td className="px-8 py-5">
                                        <p className="text-sm font-bold text-slate-700">{order.customer}</p>
                                        <p className="text-[10px] text-slate-400 font-medium">customer@example.com</p>
                                    </td>
                                    <td className="px-8 py-5 text-sm font-medium text-slate-500">{order.date}</td>
                                    <td className="px-8 py-5 font-black text-primary">{order.total.toLocaleString()}đ</td>
                                    <td className="px-8 py-5">
                                        <div className={`flex items-center gap-2 px-3 py-1 rounded-full w-fit bg-${status.color}-50 text-${status.color}-500 text-[10px] font-black uppercase`}>
                                            <StatusIcon size={12} />
                                            {status.label}
                                        </div>
                                    </td>
                                    <td className="px-8 py-5">
                                        <div className="flex items-center justify-center gap-2">
                                            <button className="p-2 text-slate-400 hover:text-primary hover:bg-blue-50 rounded-lg transition-all">
                                                <Eye size={18} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
