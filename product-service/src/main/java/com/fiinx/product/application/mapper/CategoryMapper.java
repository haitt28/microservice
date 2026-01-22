package com.fiinx.product.application.mapper;

import com.fiinx.product.domain.entity.Category;
import org.mapstruct.*;

/**
 * MapStruct Mapper cho Category
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    // Simple mapper, no complex logic needed
}
