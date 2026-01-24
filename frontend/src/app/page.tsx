'use client';

import Image from 'next/image';
import Link from 'next/link';
import { motion } from 'framer-motion';
import { ArrowRight, Zap, ShieldCheck, Star, TrendingUp } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { ProductService } from '@/services/productService';
import ProductCard from '@/components/features/ProductCard';
import FlashSale from '@/components/features/FlashSale';

export default function Home() {
  const { data: featuredProducts, isLoading } = useQuery({
    queryKey: ['featuredProducts'],
    queryFn: () => ProductService.getFeaturedProducts(),
    initialData: []
  });

  return (
    <div className="bg-white">
      {/* Modern Hero Section - Clean Typography */}
      <section className="relative min-h-[90vh] flex items-center pt-20 overflow-hidden bg-gradient-to-br from-slate-50 via-white to-blue-50/30">
        <div className="section-container grid grid-cols-1 lg:grid-cols-2 gap-16 items-center py-20">
          {/* Left: Content */}
          <motion.div
            initial={{ opacity: 0, x: -30 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.8 }}
            className="space-y-8 z-10"
          >
            <div className="inline-block">
              <span className="px-4 py-2 bg-blue-100 text-blue-600 rounded-full text-xs font-bold uppercase tracking-wider">
                The Future of Retail
              </span>
            </div>

            <h1 className="text-5xl md:text-7xl font-bold text-slate-900 leading-tight">
              Chào mừng đến với <br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
                LUXORA
              </span>
            </h1>

            <p className="text-xl text-slate-600 leading-relaxed max-w-xl">
              Nâng tầm phong cách sống với công nghệ tuyển chọn và thiết kế không thỏa hiệp. Trải nghiệm mua sắm cao cấp nhất.
            </p>

            <div className="flex flex-col sm:flex-row gap-4 pt-4">
              <Link
                href="/products"
                className="px-8 py-4 bg-slate-900 text-white rounded-2xl font-bold hover:bg-slate-800 transition-all shadow-xl hover:shadow-2xl flex items-center justify-center gap-2"
              >
                Khám phá ngay <ArrowRight size={20} />
              </Link>
              <Link
                href="/deals"
                className="px-8 py-4 bg-white border-2 border-slate-200 text-slate-900 rounded-2xl font-bold hover:border-slate-300 hover:bg-slate-50 transition-all flex items-center justify-center gap-2"
              >
                Xem ưu đãi <TrendingUp size={20} />
              </Link>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-3 gap-8 pt-8 border-t border-slate-200">
              <div>
                <p className="text-3xl font-bold text-slate-900">50K+</p>
                <p className="text-sm text-slate-500 font-medium">Khách hàng</p>
              </div>
              <div>
                <p className="text-3xl font-bold text-slate-900">1000+</p>
                <p className="text-sm text-slate-500 font-medium">Sản phẩm</p>
              </div>
              <div>
                <p className="text-3xl font-bold text-slate-900">4.9★</p>
                <p className="text-sm text-slate-500 font-medium">Đánh giá</p>
              </div>
            </div>
          </motion.div>

          {/* Right: Large Hero Image */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 1, delay: 0.2 }}
            className="relative h-[600px] rounded-3xl overflow-hidden shadow-2xl"
          >
            <Image
              src="/ecommerce_hero_banner.png"
              alt="Hero Product"
              fill
              className="object-cover"
              priority
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent" />
          </motion.div>
        </div>
      </section>

      {/* Value Propositions */}
      <section className="section-container py-20">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {[
            { icon: Zap, title: "Giao hàng 2h", desc: "Tối ưu hóa logistic cho khu vực nội thành", bgColor: "bg-amber-50", textColor: "text-amber-600" },
            { icon: ShieldCheck, title: "Bảo hành 2 năm", desc: "An tâm tuyệt đối với chính sách 1 đổi 1", bgColor: "bg-blue-50", textColor: "text-blue-600" },
            { icon: Star, title: "Chuẩn LUXORA", desc: "Mọi sản phẩm đều được kiểm duyệt gắt gao", bgColor: "bg-emerald-50", textColor: "text-emerald-600" },
          ].map((item, idx) => (
            <motion.div
              key={idx}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.1 }}
              viewport={{ once: true }}
              className="bg-white border border-slate-100 rounded-2xl p-6 hover:shadow-lg hover:border-slate-200 transition-all duration-300 group"
            >
              <div className={`inline-flex p-3 rounded-xl ${item.bgColor} ${item.textColor} mb-4 group-hover:scale-110 transition-transform`}>
                <item.icon size={24} />
              </div>
              <h3 className="text-lg font-bold text-slate-900 mb-2">{item.title}</h3>
              <p className="text-sm text-slate-600 leading-relaxed">{item.desc}</p>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Flash Sale */}
      <FlashSale />

      {/* Featured Products */}
      <section className="section-container py-20 space-y-12">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
          <div className="space-y-3">
            <h2 className="text-4xl md:text-5xl font-bold text-slate-900">Sản phẩm nổi bật</h2>
            <p className="text-slate-500 font-medium">Tuyển chọn những sản phẩm được yêu thích nhất</p>
          </div>
          <Link
            href="/products"
            className="text-blue-600 font-bold flex items-center gap-2 hover:gap-4 transition-all group"
          >
            Xem tất cả <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>

        {isLoading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {[1, 2, 3, 4].map(i => (
              <div key={i} className="aspect-[4/3] bg-slate-100 rounded-3xl animate-pulse" />
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {(featuredProducts.length > 0 ? featuredProducts : [1, 2, 3, 4]).map((product: any, idx: number) => (
              typeof product === 'number' ? (
                <div key={idx} className="aspect-[4/3] bg-slate-50 border-2 border-dashed border-slate-200 rounded-3xl flex items-center justify-center">
                  <p className="text-slate-300 font-bold text-sm">Coming Soon</p>
                </div>
              ) : (
                <ProductCard key={product.id} product={product} />
              )
            ))}
          </div>
        )}
      </section>

      {/* CTA Section */}
      <section className="section-container py-20">
        <div className="relative rounded-[48px] overflow-hidden bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 p-16 md:p-24 text-center">
          <div className="absolute inset-0 opacity-10">
            <div className="absolute top-0 left-0 w-96 h-96 bg-blue-500 rounded-full blur-3xl" />
            <div className="absolute bottom-0 right-0 w-96 h-96 bg-purple-500 rounded-full blur-3xl" />
          </div>

          <div className="relative z-10 space-y-8 max-w-3xl mx-auto">
            <h2 className="text-4xl md:text-6xl font-bold text-white leading-tight">
              Sẵn sàng cho trải nghiệm mới?
            </h2>
            <p className="text-xl text-slate-300 leading-relaxed">
              Gia nhập cộng đồng LUXORA để nhận quyền truy cập sớm vào những thiết bị giới hạn nhất
            </p>
            <div className="flex flex-col sm:flex-row gap-4 items-center justify-center max-w-xl mx-auto pt-4">
              <input
                type="email"
                placeholder="Nhập email của bạn"
                className="input-field !bg-white/10 !border-white/20 !text-white !placeholder:text-white/50 focus:!ring-white/30"
              />
              <button className="w-full sm:w-auto px-8 py-3 bg-white text-slate-900 rounded-xl font-semibold hover:bg-slate-100 transition-all whitespace-nowrap shadow-lg">
                Đăng ký ngay
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
