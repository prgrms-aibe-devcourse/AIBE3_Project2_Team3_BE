package com.pi.global.initData;

import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    @Bean
    ApplicationRunner prodCategoryInitDataRunner() {
        return args -> {
            initCategories();
        };
    }

    @Transactional
    public void initCategories() {
        if (categoryRepository.count() == 0) {
            Category itDev = new Category("IT/개발");
            itDev.addChild(new Category("웹 개발"));
            itDev.addChild(new Category("앱 개발"));
            itDev.addChild(new Category("서버/백엔드"));
            itDev.addChild(new Category("데이터/AI"));

            Category design = new Category("디자인");
            design.addChild(new Category("그래픽 디자인"));
            design.addChild(new Category("UI/UX 디자인"));
            design.addChild(new Category("제품/패키지 디자인"));

            Category marketing = new Category("마케팅");
            marketing.addChild(new Category("온라인 마케팅"));
            marketing.addChild(new Category("콘텐츠 마케팅"));
            marketing.addChild(new Category("브랜드 마케팅"));

            Category translation = new Category("번역/통역");
            translation.addChild(new Category("영어 번역"));
            translation.addChild(new Category("중국어 번역"));
            translation.addChild(new Category("일본어 번역"));

            Category video = new Category("영상/음향");
            video.addChild(new Category("영상 편집"));
            video.addChild(new Category("촬영"));
            video.addChild(new Category("음향/녹음"));

            Category writing = new Category("문서/글쓰기");
            writing.addChild(new Category("블로그/콘텐츠 작성"));
            writing.addChild(new Category("기획서 작성"));
            writing.addChild(new Category("교정/교열"));

            Category etc = new Category("기타");
            etc.addChild(new Category("비즈니스 지원"));
            etc.addChild(new Category("교육/과외"));
            etc.addChild(new Category("상담/코칭"));

            categoryRepository.save(itDev);
            categoryRepository.save(design);
            categoryRepository.save(marketing);
            categoryRepository.save(translation);
            categoryRepository.save(video);
            categoryRepository.save(writing);
            categoryRepository.save(etc);
        }
    }
}
