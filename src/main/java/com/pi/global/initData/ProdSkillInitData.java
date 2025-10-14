package com.pi.global.initData;

import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.skill.skill.repository.SkillRepository;
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
    private final SkillRepository skillRepository;

    @Bean
    ApplicationRunner prodSkillInitDataRunner() {
        return args -> {
            initSkills();
        };
    }

    @Transactional
    public void initSkills() {
        if (skillRepository.count() == 0) {
            // IT/개발
            skillRepository.save(new Skill("Java"));
            skillRepository.save(new Skill("Spring Boot"));
            skillRepository.save(new Skill("Kotlin"));
            skillRepository.save(new Skill("React"));
            skillRepository.save(new Skill("Node.js"));
            skillRepository.save(new Skill("Python"));
            skillRepository.save(new Skill("Django"));
            skillRepository.save(new Skill("AWS"));
            skillRepository.save(new Skill("MySQL"));
            skillRepository.save(new Skill("Git"));

            // 디자인
            skillRepository.save(new Skill("Photoshop"));
            skillRepository.save(new Skill("Illustrator"));
            skillRepository.save(new Skill("Figma"));
            skillRepository.save(new Skill("Sketch"));
            skillRepository.save(new Skill("XD"));

            // 마케팅
            skillRepository.save(new Skill("Google Analytics"));
            skillRepository.save(new Skill("SEO"));
            skillRepository.save(new Skill("콘텐츠 기획"));
            skillRepository.save(new Skill("SNS 마케팅"));

            // 번역/통역
            skillRepository.save(new Skill("영어"));
            skillRepository.save(new Skill("중국어"));
            skillRepository.save(new Skill("일본어"));

            // 영상/음향
            skillRepository.save(new Skill("Premiere Pro"));
            skillRepository.save(new Skill("After Effects"));
            skillRepository.save(new Skill("Final Cut Pro"));
            skillRepository.save(new Skill("Audition"));

            // 문서/글쓰기
            skillRepository.save(new Skill("MS Word"));
            skillRepository.save(new Skill("MS Excel"));
            skillRepository.save(new Skill("블로그 작성"));
            skillRepository.save(new Skill("기획서 작성"));

            // 기타
            skillRepository.save(new Skill("엑셀 자동화"));
            skillRepository.save(new Skill("교육/과외"));
            skillRepository.save(new Skill("상담/코칭"));
        }
    }
}
