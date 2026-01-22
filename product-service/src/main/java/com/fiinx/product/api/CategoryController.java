package com.fiinx.product.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.product.application.service.CategoryService;
import com.fiinx.product.domain.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Category Controller - Public endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category API")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * Get all root categories
     */
    @GetMapping
    @Operation(summary = "Get categories", description = "Get all root categories")
    public ResponseEntity<ApiResponse<List<Category>>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Get category details")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    /**
     * Get category by slug
     */
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug", description = "Get category by SEO-friendly slug")
    public ResponseEntity<ApiResponse<Category>> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    /**
     * Get child categories
     */
    @GetMapping("/{id}/children")
    @Operation(summary = "Get child categories", description = "Get sub-categories of a category")
    public ResponseEntity<ApiResponse<List<Category>>> getChildCategories(@PathVariable UUID id) {
        List<Category> children = categoryService.getChildCategories(id);
        return ResponseEntity.ok(ApiResponse.success(children));
    }
    
    /**
     * Get all active categories (flat list)
     */
    @GetMapping("/all")
    @Operation(summary = "Get all categories", description = "Get all active categories in flat list")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
