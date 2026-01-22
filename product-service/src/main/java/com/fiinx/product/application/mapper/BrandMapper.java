package com.fiinx.product.application.mapper;

import com.fiinx.product.domain.entity.Brand;
import org.mapstruct.*;

/**
 * MapStruct Mapper cho Brand
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {
    // Simple mapper, no complex logic needed
}
