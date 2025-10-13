package com.pi.domain.category.category.repository;

import com.pi.domain.category.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();

    Page<Category> findByParentIsNull(Pageable pageable);

    List<Category> findByParentId(Long parentId);
}
