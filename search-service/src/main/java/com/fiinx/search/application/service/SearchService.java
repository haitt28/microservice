package com.fiinx.search.application.service;

import com.fiinx.search.domain.model.ProductIndex;
import com.fiinx.search.domain.repository.ProductIndexRepository;
import com.fiinx.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductIndexRepository productIndexRepository;

    public PageResponse<ProductIndex> search(String keyword, Pageable pageable) {
        log.info("Searching for: {}", keyword);
        Page<ProductIndex> page = productIndexRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
        
        return PageResponse.<ProductIndex>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public void indexProduct(ProductIndex product) {
        log.info("Indexing product: {}", product.getName());
        productIndexRepository.save(product);
    }

    public void removeProduct(String id) {
        log.info("Removing product from index: {}", id);
        productIndexRepository.deleteById(id);
    }
}
