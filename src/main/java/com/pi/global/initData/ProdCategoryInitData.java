package com.pi.global.initData;

import com.pi.domain.category.category.dto.CategoryCreateReqBody;
import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class ProdCategoryInitData {
    private final CategoryService categoryService;

    @Bean
    ApplicationRunner prodCategoryInitDataRunner() {
        return args -> {
            initCategories();
        };
    }

    @Transactional
    public void initCategories() {
        if (categoryService.count() > 0) {
            return;
        }

        Map<String, List<String>> categoryMap = new LinkedHashMap<>();
        categoryMap.put("IT/개발", List.of("웹 개발", "앱 개발", "서버/백엔드", "데이터/AI"));
        categoryMap.put("디자인", List.of("그래픽 디자인", "UI/UX 디자인", "제품/패키지 디자인"));
        categoryMap.put("마케팅", List.of("온라인 마케팅", "콘텐츠 마케팅", "브랜드 마케팅"));
        categoryMap.put("번역/통역", List.of("영어 번역", "중국어 번역", "일본어 번역"));
        categoryMap.put("영상/음향", List.of("영상 편집", "촬영", "음향/녹음"));
        categoryMap.put("문서/글쓰기", List.of("블로그/콘텐츠 작성", "기획서 작성", "교정/교열"));
        categoryMap.put("기타", List.of("비즈니스 지원", "교육/과외", "상담/코칭"));

        for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
            Category parent = categoryService.create(new CategoryCreateReqBody(entry.getKey(), null));
            for (String child : entry.getValue()) {
                categoryService.create(new CategoryCreateReqBody(child, parent.getId()));
            }
        }
    }
}