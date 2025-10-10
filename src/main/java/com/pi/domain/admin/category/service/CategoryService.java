package com.pi.domain.admin.category.service;

import com.pi.domain.admin.category.dto.CategoryCreateReqBody;
import com.pi.domain.admin.category.dto.CategoryResBody;
import com.pi.domain.admin.category.entity.Category;
import com.pi.domain.admin.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(CategoryCreateReqBody dto) {
        Category category = new Category();
        category.setName(dto.name());

        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .get();
            parent.addChild(category);
        }

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResBody> getCategoryTree() {
        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream()
                .map(CategoryResBody::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}