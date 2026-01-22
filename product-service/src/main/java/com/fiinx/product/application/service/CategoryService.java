package com.fiinx.product.application.service;

import com.fiinx.common.exception.NotFoundException;
import com.fiinx.product.domain.entity.Category;
import com.fiinx.product.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Senior Note: Category Service
 * 
 * - Quản lý hierarchical categories
 * - Cache toàn bộ tree cho performance
 * - Helper methods để navigate tree
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Lấy tất cả root categories
     */
    @Cacheable(value = "categoryTree", key = "'root'")
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrue();
    }
    
    /**
     * Lấy category theo ID
     */
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }
    
    /**
     * Lấy category theo slug
     */
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException("Category not found with slug: " + slug));
    }
    
    /**
     * Lấy children của category
     */
    public List<Category> getChildCategories(UUID parentId) {
        return categoryRepository.findByParentIdAndActiveTrue(parentId);
    }
    
    /**
     * Lấy tất cả active categories
     */
    @Cacheable(value = "categoryList")
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }
}
