package com.pi.domain.admin.category.controller;

import com.pi.domain.admin.category.dto.CategoryCreateReqBody;
import com.pi.domain.admin.category.dto.CategoryResBody;
import com.pi.domain.admin.category.entity.Category;
import com.pi.domain.admin.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
@Tag(name = "ApiV1AdminCategoryController", description = "API 관리자 카테고리 컨트롤러")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성")
    @PostMapping
    public CategoryResBody createCategory(@RequestBody CategoryCreateReqBody dto) {
        Category category = categoryService.createCategory(dto);
        return CategoryResBody.from(category, List.of());
    }

    @GetMapping
    @Operation(summary = "전체 카테고리 트리 조회")
    public List<CategoryResBody> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @Operation(summary = "카테고리 삭제")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
