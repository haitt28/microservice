'use client';

import { motion } from 'framer-motion';
import Navbar from '@/components/shared/Navbar';
import ChatWidget from '@/components/shared/ChatWidget';

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
