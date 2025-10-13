package com.pi.domain.category.category.controller;

import com.pi.domain.category.category.dto.CategoryTreeDto;
import com.pi.domain.category.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@Tag(name = "ApiV1CategoryController", description = "API 카테고리 컨트롤러")
public class ApiV1CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "전체 카테고리 조회")
    public List<CategoryTreeDto> getTreeAll() {
        return categoryService.getTreeAll();
    }
}
