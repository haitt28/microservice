'use client';

import { InputHTMLAttributes, forwardRef } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
    required?: boolean;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
    ({ label, error, required, className = '', ...props }, ref) => {
        return (
            <div className="w-full">
                {label && (
                    <label className="block text-sm font-semibold text-slate-700 mb-2">
                        {label} {required && <span className="text-rose-500">*</span>}
                    </label>
                )}
                <input
                    ref={ref}
                    className={`input-field ${className}`}
                    {...props}
                />
                {error && (
                    <p className="mt-1.5 text-sm text-rose-500">{error}</p>
                )}
            </div>
        );
    }
);

Input.displayName = 'Input';

export default Input;
