package com.pi.global.initData;

import com.pi.domain.skill.skill.dto.SkillCreateReqBody;
import com.pi.domain.skill.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class ProdSkillInitData {
    private final SkillService skillService;

    @Bean
    ApplicationRunner prodSkillInitDataRunner() {
        return args -> {
            initSkills();
        };
    }

    @Transactional
    public void initSkills() {
        if (skillService.count() == 0) {
            // IT/개발
            skillService.create(new SkillCreateReqBody("Java"));
            skillService.create(new SkillCreateReqBody("Spring Boot"));
            skillService.create(new SkillCreateReqBody("Kotlin"));
            skillService.create(new SkillCreateReqBody("React"));
            skillService.create(new SkillCreateReqBody("Node.js"));
            skillService.create(new SkillCreateReqBody("Python"));
            skillService.create(new SkillCreateReqBody("Django"));
            skillService.create(new SkillCreateReqBody("AWS"));
            skillService.create(new SkillCreateReqBody("MySQL"));
            skillService.create(new SkillCreateReqBody("Git"));

            // 디자인
            skillService.create(new SkillCreateReqBody("Photoshop"));
            skillService.create(new SkillCreateReqBody("Illustrator"));
            skillService.create(new SkillCreateReqBody("Figma"));
            skillService.create(new SkillCreateReqBody("Sketch"));
            skillService.create(new SkillCreateReqBody("XD"));

            // 마케팅
            skillService.create(new SkillCreateReqBody("Google Analytics"));
            skillService.create(new SkillCreateReqBody("SEO"));
            skillService.create(new SkillCreateReqBody("콘텐츠 기획"));
            skillService.create(new SkillCreateReqBody("SNS 마케팅"));

            // 번역/통역
            skillService.create(new SkillCreateReqBody("영어"));
            skillService.create(new SkillCreateReqBody("중국어"));
            skillService.create(new SkillCreateReqBody("일본어"));

            // 영상/음향
            skillService.create(new SkillCreateReqBody("Premiere Pro"));
            skillService.create(new SkillCreateReqBody("After Effects"));
            skillService.create(new SkillCreateReqBody("Final Cut Pro"));
            skillService.create(new SkillCreateReqBody("Audition"));

            // 문서/글쓰기
            skillService.create(new SkillCreateReqBody("MS Word"));
            skillService.create(new SkillCreateReqBody("MS Excel"));
            skillService.create(new SkillCreateReqBody("블로그 작성"));
            skillService.create(new SkillCreateReqBody("기획서 작성"));

            // 기타
            skillService.create(new SkillCreateReqBody("엑셀 자동화"));
            skillService.create(new SkillCreateReqBody("교육/과외"));
            skillService.create(new SkillCreateReqBody("상담/코칭"));
        }
    }
}
