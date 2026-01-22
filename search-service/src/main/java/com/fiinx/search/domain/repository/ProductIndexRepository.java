package com.fiinx.search.domain.repository;

import com.fiinx.search.domain.model.ProductIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductIndexRepository extends ElasticsearchRepository<ProductIndex, String> {
    Page<ProductIndex> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
}
