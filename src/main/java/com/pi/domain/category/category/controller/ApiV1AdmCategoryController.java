package com.pi.domain.category.category.controller;

import com.pi.domain.category.category.dto.CategoryCreateReqBody;
import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.service.CategoryService;
import com.pi.global.rsData.PagePayload;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
@Tag(name = "ApiV1AdminCategoryController", description = "API 관리자 카테고리 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
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
    public PagePayload<CategoryDto> getCategories(
            Authentication authentication,
            @ParameterObject @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        System.out.println("principal=" + authentication.getName());
        System.out.println("authorities=" + authentication.getAuthorities());
        return Ut.pageMapper.of(categoryService.getCategories(pageable));
    }

    @Operation(summary = "카테고리 삭제")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
