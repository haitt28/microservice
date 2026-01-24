'use client';

import { useState } from 'react';
import { MessageCircle, X, Send, User, Bot } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export default function ChatWidget() {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([
        { id: 1, text: 'Xin chào! LUXORA có thể giúp gì cho bạn?', sender: 'bot' }
    ]);
    const [input, setInput] = useState('');

    const handleSend = () => {
        if (!input.trim()) return;
        setMessages([...messages, { id: Date.now(), text: input, sender: 'user' }]);
        setInput('');

        // Mock bot response
        setTimeout(() => {
            setMessages(prev => [...prev, { id: Date.now() + 1, text: 'Cảm ơn bạn đã quan tâm. Chuyên viên sẽ hỗ trợ bạn ngay!', sender: 'bot' }]);
        }, 1000);
    };

    return (
        <div className="fixed bottom-8 right-8 z-[100]">
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.8, y: 20 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.8, y: 20 }}
                        className="glass-card mb-6 w-80 sm:w-96 h-[500px] flex flex-col overflow-hidden shadow-2xl border-primary/10"
                    >
                        {/* Header */}
                        <div className="bg-primary p-6 text-white flex justify-between items-center">
                            <div className="flex items-center gap-3">
                                <div className="p-2 bg-white/10 rounded-xl relative">
                                    <Bot size={24} />
                                    <span className="absolute bottom-0 right-0 w-3 h-3 bg-success border-2 border-primary rounded-full"></span>
                                </div>
                                <div>
                                    <h4 className="font-bold text-sm">Hỗ trợ trực tuyến</h4>
                                    <p className="text-[10px] text-slate-300 font-medium tracking-widest uppercase">Trực tuyến 24/7</p>
                                </div>
                            </div>
                            <button onClick={() => setIsOpen(false)} className="hover:bg-white/10 p-2 rounded-lg transition-colors">
                                <X size={20} />
                            </button>
                        </div>

                        {/* Messages */}
                        <div className="flex-1 overflow-y-auto p-6 space-y-4 bg-slate-50/50">
                            {messages.map((msg) => (
                                <div key={msg.id} className={`flex ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
                                    <div className={`max-w-[80%] p-4 rounded-2xl text-sm font-medium ${msg.sender === 'user'
                                        ? 'bg-primary text-white rounded-tr-none'
                                        : 'bg-white text-slate-700 shadow-sm rounded-tl-none border border-slate-100'
                                        }`}>
                                        {msg.text}
                                    </div>
                                </div>
                            ))}
                        </div>

                        {/* Input */}
                        <div className="p-4 bg-white border-t border-slate-100 flex gap-2">
                            <input
                                type="text"
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                onKeyPress={(e) => e.key === 'Enter' && handleSend()}
                                placeholder="Nhập tin nhắn..."
                                className="flex-1 bg-slate-50 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-1 focus:ring-primary"
                            />
                            <button
                                onClick={handleSend}
                                className="p-3 bg-primary text-white rounded-xl hover:bg-slate-800 transition-colors"
                            >
                                <Send size={18} />
                            </button>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>

            <button
                onClick={() => setIsOpen(!isOpen)}
                className="w-16 h-16 bg-primary text-white rounded-[24px] flex items-center justify-center shadow-2xl hover:bg-slate-800 transition-all transform hover:scale-110 active:scale-95 group"
            >
                {isOpen ? <X size={28} /> : (
                    <>
                        <MessageCircle size={28} className="group-hover:rotate-12 transition-transform" />
                        <span className="absolute -top-1 -right-1 w-5 h-5 bg-accent rounded-full border-4 border-white"></span>
                    </>
                )}
            </button>
        </div>
    );
}
