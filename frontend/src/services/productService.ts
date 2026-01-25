import api from '../lib/api';
import { Product, Category } from '../types';

// Tiện ích xử lý lỗi API mềm dẻo (gracefully) trong môi trường phát triển
const handleApiError = (error: any, fallback: any) => {
    console.warn('[API Warning] Sử dụng dữ liệu mẫu (fallback) vì không thể kết nối tới Backend:', error.message);
    return fallback;
};

export const ProductService = {
    getProducts: async (params?: any) => {
        try {
            const response = await api.get<Product[]>('/products', { params });
            return response.data;
        } catch (e) {
            return handleApiError(e, []);
        }
    },

    getProductBySlug: async (slug: string) => {
        try {
            const response = await api.get<Product>(`/products/${slug}`);
            return response.data;
        } catch (e) {
            // Trả về một sản phẩm mẫu cho mục đích demo nếu API thất bại
            return handleApiError(e, {
                id: 'mock-1',
                slug: 'minimalist-smart-watch-v2',
                name: 'Minimalist Smart Watch V2',
                description: 'Bản thiết kế tối giản, chất liệu cao cấp từ thép không gỉ và kính sapphire.',
                price: 5200000,
                originalPrice: 6000000,
                thumbnail: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80\u0026w=1000\u0026auto=format\u0026fit=crop',
                images: [
                    'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80\u0026w=1000\u0026auto=format\u0026fit=crop',
                    'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?q=80\u0026w=1000\u0026auto=format\u0026fit=crop'
                ],
                stock: 15,
                soldCount: 1234,
                rating: 4.8,
                category: { id: '1', name: 'Phụ kiện', slug: 'phu-kien' }
            });
        }
    },

    getFeaturedProducts: async () => {
        try {
            const response = await api.get<Product[]>('/products/featured');
            return response.data;
        } catch (e) {
            return handleApiError(e, [
                {
                    id: 'mock-1',
                    slug: 'fiinx-pro-headphone',
                    name: 'Fiinx Pro Wireless',
                    price: 2490000,
                    salePrice: 1990000,
                    thumbnail: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80',
                    category: { name: 'Âm thanh' }
                },
                {
                    id: 'mock-2',
                    slug: 'smart-watch-v2',
                    name: 'Smart Watch V2',
                    price: 5200000,
                    thumbnail: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80',
                    category: { name: 'Phụ kiện' }
                }
            ]);
        }
    },

    getCategories: async () => {
        try {
            const response = await api.get<Category[]>('/categories');
            return response.data;
        } catch (e) {
            return handleApiError(e, []);
        }
    }
};
