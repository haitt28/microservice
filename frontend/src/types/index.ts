export interface Product {
    id: string;
    name: string;
    slug: string;
    description: string;
    price: number;
    salePrice?: number;
    thumbnail: string;
    images: string[];
    stock: number;
    soldCount: number;
    rating: number;
    reviewCount: number;
    category: Category;
    attributes: ProductAttribute[];
}

export interface Category {
    id: string;
    name: string;
    slug: string;
    icon?: string;
}

export interface ProductAttribute {
    name: string;
    value: string;
    type: 'color' | 'size' | 'text';
}

export interface CartItem extends Product {
    quantity: number;
    selectedSize?: string;
    selectedColor?: string;
}

export interface Order {
    id: string;
    items: CartItem[];
    totalAmount: number;
    status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'COMPLETED' | 'CANCELLED';
    createdAt: string;
    shippingAddress: string;
}
