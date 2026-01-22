package com.fiinx.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BEST PRACTICE #2: Pagination Response
 * 
 * - Standardized pagination across all list endpoints
 * - Chứa đủ thông tin cho client xây dựng UI pagination
 * - Support cả offset-based và cursor-based pagination
 * 
 * @param <T> Type of items in the page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {
    
    @Schema(description = "List of items in current page")
    private List<T> content;
    
    @Schema(description = "Pagination metadata")
    private PageInfo page;
    
    // ==================== Factory Methods ====================
    
    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, 
                                          long totalElements, int totalPages) {
        return PageResponse.<T>builder()
                .content(content)
                .page(PageInfo.builder()
                        .number(pageNumber)
                        .size(pageSize)
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .first(pageNumber == 0)
                        .last(pageNumber >= totalPages - 1)
                        .hasNext(pageNumber < totalPages - 1)
                        .hasPrevious(pageNumber > 0)
                        .build())
                .build();
    }
    
    /**
     * Convert from Spring Data Page
     */
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> springPage) {
        return of(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages()
        );
    }
    
    // ==================== Nested Class ====================
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pagination metadata")
    public static class PageInfo {
        
        @Schema(description = "Current page number (0-indexed)", example = "0")
        private int number;
        
        @Schema(description = "Page size", example = "20")
        private int size;
        
        @Schema(description = "Total number of elements", example = "100")
        private long totalElements;
        
        @Schema(description = "Total number of pages", example = "5")
        private int totalPages;
        
        @Schema(description = "Is this the first page?")
        private boolean first;
        
        @Schema(description = "Is this the last page?")
        private boolean last;
        
        @Schema(description = "Are there more pages after this?")
        private boolean hasNext;
        
        @Schema(description = "Are there pages before this?")
        private boolean hasPrevious;
    }
}
