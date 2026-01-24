'use client';

import { TextareaHTMLAttributes, forwardRef } from 'react';

interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
    label?: string;
    error?: string;
    required?: boolean;
}

const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
    ({ label, error, required, className = '', ...props }, ref) => {
        return (
            <div className="w-full">
                {label && (
                    <label className="block text-sm font-semibold text-slate-700 mb-2">
                        {label} {required && <span className="text-rose-500">*</span>}
                    </label>
                )}
                <textarea
                    ref={ref}
                    className={`input-field resize-none ${className}`}
                    {...props}
                />
                {error && (
                    <p className="mt-1.5 text-sm text-rose-500">{error}</p>
                )}
            </div>
        );
    }
);

Textarea.displayName = 'Textarea';

export default Textarea;
