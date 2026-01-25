'use client';

import { motion } from 'framer-motion';
import Navbar from '@/components/shared/Navbar';
import ChatWidget from '@/components/shared/ChatWidget';

/**
 * Senior Note: User Layout - Layout chính cho khu vực người dùng.
 * 
 * - Bao bọc bởi Navbar ở phía trên và ChatWidget ở góc dưới.
 * - Sử dụng Framer Motion để tạo hiệu ứng Fade-in khi chuyển trang (Page Transition).
 * - Đảm bảo chiều cao tối thiểu (min-h) để Footer luôn nằm ở cuối trang.
 */

export default function UserLayout({ children }: { children: React.ReactNode }) {
    return (
        <>
            <Navbar />
            <main className="min-h-[calc(100vh-80px)] overflow-x-hidden">
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ duration: 0.5 }}
                >
                    {children}
                </motion.div>
            </main>
            <ChatWidget />
        </>
    );
}
