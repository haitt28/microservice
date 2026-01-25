import axios from 'axios';
import { getSession } from 'next-auth/react';

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor cho Authentication - Đính kèm Access Token vào Header
api.interceptors.request.use(async (config) => {
    const session: any = await getSession();
    if (session?.accessToken) {
        config.headers.Authorization = `Bearer ${session.accessToken}`;
    }
    return config;
});

// Interceptor xử lý lỗi API tập trung
api.interceptors.response.use(
    (response) => response,
    (error) => {
        const message = error.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại sau.';
        console.error('[API Error]', message);
        return Promise.reject(error);
    }
);

export default api;
