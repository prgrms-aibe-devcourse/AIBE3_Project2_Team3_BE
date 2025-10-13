package com.pi.domain.category.category.service;

import com.pi.domain.category.category.dto.CategoryCreateReqBody;
import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        boolean exists;
        Category parent = null;

        if (dto.parentId() != null) {
            parent = categoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리 없음"));

            if (parent.getParent() != null) {
                throw new IllegalArgumentException("자식 카테고리에는 하위 카테고리를 추가할 수 없습니다");
            }

            exists = categoryRepository.findByParentId(dto.parentId())
                    .stream().anyMatch(c -> c.getName().equals(dto.name()));
        } else {
            exists = categoryRepository.findByParentIsNull()
                    .stream().anyMatch(c -> c.getName().equals(dto.name()));
        }
        if (exists) throw new IllegalArgumentException("동일 이름 카테고리 존재");

        Category category = new Category();
        category.setName(dto.name());
        if (parent != null) parent.addChild(category);

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryDto> getCategoryTree(Pageable pageable) {
        return categoryRepository.findByParentIsNull(pageable)
                .map(this::buildTree);
    }

    private CategoryDto buildTree(Category category) {
        List<CategoryDto> children = categoryRepository.findByParentId(category.getId()).stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
        return new CategoryDto(
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