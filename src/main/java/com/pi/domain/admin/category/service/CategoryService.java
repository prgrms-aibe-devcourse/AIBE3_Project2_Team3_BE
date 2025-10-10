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
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리가 존재하지 않음."));
            boolean exists = categoryRepository.findByParentId(dto.parentId())
                    .stream().anyMatch(c -> c.getName().equals(dto.name()));
            if (exists) throw new IllegalArgumentException("동일한 이름의 카테고리 존재.");
            parent.addChild(category);
        } else {
            boolean exists = categoryRepository.findByParentIsNull()
                    .stream().anyMatch(c -> c.getName().equals(dto.name()));
            if (exists) throw new IllegalArgumentException("동일한 이름의 루트 카테고리 존재.");
        }

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResBody> getCategoryTree() {
        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
    }

    private CategoryResBody buildTree(Category category) {
        List<CategoryResBody> children = categoryRepository.findByParentId(category.getId()).stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
        return new CategoryResBody(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                children
        );
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}