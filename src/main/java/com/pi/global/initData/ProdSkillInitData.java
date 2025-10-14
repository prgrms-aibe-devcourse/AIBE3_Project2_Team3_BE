package com.pi.global.initData;

import com.pi.domain.skill.skill.dto.SkillCreateReqBody;
import com.pi.domain.skill.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Configuration
public class ProdSkillInitData {
    private final SkillService skillService;

    @Transactional
    public void initSkills() {
        if (skillService.count() > 0) {
            return;
        }

        String[] skills = {
                // IT/개발
                "Java", "Spring Boot", "Kotlin", "React", "Node.js", "Python", "Django", "AWS", "MySQL", "Git",
                // 디자인
                "Photoshop", "Illustrator", "Figma", "Sketch", "XD",
                // 마케팅
                "Google Analytics", "SEO", "콘텐츠 기획", "SNS 마케팅",
                // 번역/통역
                "영어", "중국어", "일본어",
                // 영상/음향
                "Premiere Pro", "After Effects", "Final Cut Pro", "Audition",
                // 문서/글쓰기
                "MS Word", "MS Excel", "블로그 작성", "기획서 작성",
                // 기타
                "엑셀 자동화", "교육/과외", "상담/코칭"
        };

        for (String skill : skills) {
            skillService.create(new SkillCreateReqBody(skill));
        }
    }
}