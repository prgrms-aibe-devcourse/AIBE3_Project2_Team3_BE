package com.pi.domain.category.category.service;

import com.pi.domain.category.category.dto.CategoryCreateReqBody;
import com.pi.domain.category.category.dto.CategoryTreeDto;
import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.repository.CategoryQueryRepository;
import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    public long count() {
        return categoryRepository.count();
    }

    @Transactional
    public Category create(CategoryCreateReqBody reqBody) {
        Category parent = null;
        if (reqBody.parentId() != null) {
            parent = categoryRepository.findById(reqBody.parentId()).orElseThrow(
                    () -> new ServiceException("400-1", "존재하지 않는 부모 카테고리입니다.")
            );
            // ✅ 손자 금지: 부모의 parent가 있으면 거부
            if (parent.getParent() != null) {
                throw new ServiceException("400-2", "하위 레벨은 1까지만 가능합니다.");
            }
        }

        return categoryRepository.save(new Category(reqBody.name().trim(), parent));
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeDto> getTreeAll() {
        List<Category> parents = categoryQueryRepository.findParentsWithChildrenAll(); // fetch join

        return parents.stream().map(p -> {
            // 부모 기본 생성
            CategoryTreeDto parent = CategoryTreeDto.parent(p.getId(), p.getName());

            // 자식들 매핑 + 정렬(원하면)
            List<CategoryTreeDto> children = p.getChildren().stream()
                    .sorted(Comparator.comparing(Category::getId).reversed())
                    .map(c -> CategoryTreeDto.child(c.getId(), c.getName(), p.getId()))
                    .toList();

            // 자식 채워서 새 레코드 반환(childCount 자동 반영)
            return parent.withChildren(children);
        }).toList();
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}