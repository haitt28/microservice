package com.fiinx.product.application.service;

import com.fiinx.common.exception.NotFoundException;
import com.fiinx.product.domain.entity.Brand;
import com.fiinx.product.domain.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Senior Note: Brand Service
 * 
 * - Quản lý brands
 * - Cache list để giảm DB queries
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    
    private final BrandRepository brandRepository;
    
    /**
     * Lấy brand theo ID
     */
    public Brand getBrandById(UUID id) {
        return brandRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Brand not found: " + id));
    }
    
    /**
     * Lấy brand theo slug
     */
    public Brand getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException("Brand not found with slug: " + slug));
    }
    
    /**
     * Lấy tất cả active brands
     */
    @Cacheable(value = "brandList")
    public List<Brand> getAllActiveBrands() {
        return brandRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }
    
    /**
     * Search brands theo tên
     */
    public List<Brand> searchBrands(String name) {
        return brandRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }
}
