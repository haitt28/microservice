import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { Product } from '@/types';

/**
 * Senior Note: Cart Store - Quản lý giỏ hàng sử dụng Zustand.
 * 
 * - Cart được lưu trữ (persist) vào LocalStorage để duy trì dữ liệu qua các phiên làm việc.
 * - Các action addItem, removeItem, updateQuantity được thiết kế để xử lý Logic đồng bộ.
 * - Tự động tính toán tổng số lượng và tổng giá trị giỏ hàng thông qua get().
 */

interface CartItem extends Product {
    quantity: number;
}

interface CartStore {
    items: CartItem[];
    addItem: (product: Product, quantity?: number) => void;
    removeItem: (productId: string) => void;
    updateQuantity: (productId: string, quantity: number) => void;
    clearCart: () => void;
    totalItems: () => number;
    totalPrice: () => number;
}

export const useCartStore = create<CartStore>()(
    persist(
        (set, get) => ({
            items: [],
            addItem: (product, quantity = 1) => {
                set((state) => {
                    const existingItem = state.items.find((item) => item.id === product.id);
                    if (existingItem) {
                        return {
                            items: state.items.map((item) =>
                                item.id === product.id
                                    ? { ...item, quantity: item.quantity + quantity }
                                    : item
                            ),
                        };
                    }
                    return { items: [...state.items, { ...product, quantity }] };
                });
            },
            removeItem: (productId) => {
                set((state) => ({
                    items: state.items.filter((item) => item.id !== productId),
                }));
            },
            updateQuantity: (productId, quantity) => {
                set((state) => ({
                    items: state.items.map((item) =>
                        item.id === productId ? { ...item, quantity } : item
                    ),
                }));
            },
            clearCart: () => set({ items: [] }),
            totalItems: () => get().items.reduce((acc, item) => acc + item.quantity, 0),
            totalPrice: () =>
                get().items.reduce(
                    (acc, item) => acc + (item.salePrice || item.price) * item.quantity,
                    0
                ),
        }),
        {
            name: 'fiinx-cart-storage',
        }
    )
);
