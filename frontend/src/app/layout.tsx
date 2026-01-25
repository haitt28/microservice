import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'
import ClientLayout from '@/components/shared/ClientLayout'
import UserLayout from '@/components/shared/UserLayout'
import Link from 'next/link'

const inter = Inter({
  subsets: ['latin', 'vietnamese'],
  weight: ['300', '400', '500', '600', '700', '800', '900'],
  variable: '--font-inter',
})

export const metadata: Metadata = {
  title: 'LUXORA | Trải nghiệm mua sắm xa xỉ',
  description: 'Nâng tầm phong cách sống với các thiết bị công nghệ và đồ dùng cao cấp hàng đầu.',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="vi" className={`${inter.variable} scroll-smooth scroll-pt-32`}>
      <body className={`${inter.className} bg-white antialiased`}>
        <ClientLayout>
          <UserLayout>
            <div className="flex flex-col min-h-screen">
              <main className="flex-grow">
                {children}
              </main>
              <footer className="bg-slate-900 text-white py-20">
                <div className="section-container grid grid-cols-1 md:grid-cols-4 gap-12">
                  <div className="space-y-6">
                    <h3 className="text-3xl font-black tracking-tighter text-white">LUXORA<span className="text-amber-400">.</span></h3>
                    <p className="text-slate-400 text-sm leading-relaxed">
                      Nâng tầm trải nghiệm mua sắm của bạn với các sản phẩm tuyển chọn và dịch vụ tận tâm nhất.
                    </p>
                  </div>
                  <div>
                    <h4 className="font-bold text-lg mb-6">Mua sắm</h4>
                    <ul className="text-slate-400 text-sm space-y-4">
                      <li><Link href="/products" className="hover:text-accent transition-colors">Tất cả sản phẩm</Link></li>
                      <li><Link href="/categories" className="hover:text-accent transition-colors">Danh mục</Link></li>
                      <li><Link href="/deals" className="hover:text-accent transition-colors">Giảm giá</Link></li>
                    </ul>
                  </div>
                  <div>
                    <h4 className="font-bold text-lg mb-6">Hỗ trợ</h4>
                    <ul className="text-slate-400 text-sm space-y-4">
                      <li><Link href="/contact" className="hover:text-accent transition-colors">Liên hệ</Link></li>
                      <li><Link href="/faq" className="hover:text-accent transition-colors">Câu hỏi thường gặp</Link></li>
                      <li><Link href="/shipping" className="hover:text-accent transition-colors">Chính sách giao hàng</Link></li>
                    </ul>
                  </div>
                  <div>
                    <h4 className="font-bold text-lg mb-6">Bản tin</h4>
                    <p className="text-slate-400 text-sm mb-6">Đăng ký để nhận ưu đãi đặc quyền.</p>
                    <div className="flex flex-col gap-3">
                      <input type="email" placeholder="Email của bạn" className="bg-slate-800 border-none rounded-2xl px-6 py-3 text-sm focus:ring-2 focus:ring-accent transition-all" />
                      <button className="bg-accent text-white px-6 py-3 rounded-2xl text-sm font-bold hover:brightness-110 active:scale-95 transition-all w-full">Đăng ký</button>
                    </div>
                  </div>
                </div>
                <div className="section-container border-t border-slate-800 mt-20 pt-10 flex flex-col md:flex-row justify-between items-center gap-4 text-slate-400 text-xs font-medium">
                  <span>© {new Date().getFullYear()} LUXORA E-commerce Platform.</span>
                  <div className="flex gap-8">
                    <Link href="/privacy" className="hover:text-white transition-colors">Quyền riêng tư</Link>
                    <Link href="/terms" className="hover:text-white transition-colors">Điều khoản</Link>
                  </div>
                </div>
              </footer>
            </div>
          </UserLayout>
        </ClientLayout>
      </body>
    </html>
  )
}
