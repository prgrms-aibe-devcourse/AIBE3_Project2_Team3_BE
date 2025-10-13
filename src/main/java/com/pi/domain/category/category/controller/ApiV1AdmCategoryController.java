package com.pi.domain.category.category.controller;

import com.pi.domain.category.category.dto.CategoryCreateReqBody;
import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
@Tag(name = "ApiV1AdminCategoryController", description = "API 관리자 카테고리 컨트롤러")
public class ApiV1AdmCategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성")
    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryCreateReqBody dto) {
        Category category = categoryService.createCategory(dto);
        return CategoryDto.from(category, List.of());
    }

    @GetMapping
    @Operation(summary = "전체 카테고리 조회")
    public Page<CategoryDto> getCategories(Pageable pageable) {
        return categoryService.getCategories(pageable);
    }

    @Operation(summary = "카테고리 삭제")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
