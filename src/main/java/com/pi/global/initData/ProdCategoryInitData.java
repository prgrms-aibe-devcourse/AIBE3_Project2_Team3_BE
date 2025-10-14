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
        if (categoryService.count() == 0) {
            Category itDev = categoryService.create(new CategoryCreateReqBody("IT/개발", null));
            categoryService.create(new CategoryCreateReqBody("웹 개발", itDev.getId()));
            categoryService.create(new CategoryCreateReqBody("앱 개발", itDev.getId()));
            categoryService.create(new CategoryCreateReqBody("서버/백엔드", itDev.getId()));
            categoryService.create(new CategoryCreateReqBody("데이터/AI", itDev.getId()));

            Category design = categoryService.create(new CategoryCreateReqBody("디자인", null));
            categoryService.create(new CategoryCreateReqBody("그래픽 디자인", design.getId()));
            categoryService.create(new CategoryCreateReqBody("UI/UX 디자인", design.getId()));
            categoryService.create(new CategoryCreateReqBody("제품/패키지 디자인", design.getId()));

            Category marketing = categoryService.create(new CategoryCreateReqBody("마케팅", null));
            categoryService.create(new CategoryCreateReqBody("온라인 마케팅", marketing.getId()));
            categoryService.create(new CategoryCreateReqBody("콘텐츠 마케팅", marketing.getId()));
            categoryService.create(new CategoryCreateReqBody("브랜드 마케팅", marketing.getId()));

            Category translation = categoryService.create(new CategoryCreateReqBody("번역/통역", null));
            categoryService.create(new CategoryCreateReqBody("영어 번역", translation.getId()));
            categoryService.create(new CategoryCreateReqBody("중국어 번역", translation.getId()));
            categoryService.create(new CategoryCreateReqBody("일본어 번역", translation.getId()));

            Category video = categoryService.create(new CategoryCreateReqBody("영상/음향", null));
            categoryService.create(new CategoryCreateReqBody("영상 편집", video.getId()));
            categoryService.create(new CategoryCreateReqBody("촬영", video.getId()));
            categoryService.create(new CategoryCreateReqBody("음향/녹음", video.getId()));

            Category writing = categoryService.create(new CategoryCreateReqBody("문서/글쓰기", null));
            categoryService.create(new CategoryCreateReqBody("블로그/콘텐츠 작성", writing.getId()));
            categoryService.create(new CategoryCreateReqBody("기획서 작성", writing.getId()));
            categoryService.create(new CategoryCreateReqBody("교정/교열", writing.getId()));

            Category etc = categoryService.create(new CategoryCreateReqBody("기타", null));
            categoryService.create(new CategoryCreateReqBody("비즈니스 지원", etc.getId()));
            categoryService.create(new CategoryCreateReqBody("교육/과외", etc.getId()));
            categoryService.create(new CategoryCreateReqBody("상담/코칭", etc.getId()));
        }
    }
}
