package com.fiinx.search.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.common.dto.PageResponse;
import com.fiinx.search.application.service.SearchService;
import com.fiinx.search.domain.model.ProductIndex;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Advanced search API using ElasticSearch")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Search products", description = "Search products by keyword in name and description")
    public ResponseEntity<ApiResponse<PageResponse<ProductIndex>>> search(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(searchService.search(q, pageable)));
    }
}
