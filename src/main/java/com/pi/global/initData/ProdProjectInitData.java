package com.pi.global.initData;

import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.post.project.dto.ProjectWriteDto;
import com.pi.domain.post.project.service.ProjectService;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class ProdProjectInitData {
    private final ProjectService projectService;
    private final UserRepository userRepository;

    @Bean
    ApplicationRunner prodProjectInitDataRunner() {
        return args -> {
            initProjects();
        };
    }

    @Transactional
    public void initProjects() {
        if (projectService.count() == 0) {
            User user1 = userRepository.findById(2L).orElseThrow();
            User user2 = userRepository.findById(3L).orElseThrow();
            User user3 = userRepository.findById(4L).orElseThrow();

            projectService.create(
                    user1,
                    new PostWriteDto("웹 개발 프로젝트", "React 기반 웹 개발", true),
                    new ProjectWriteDto(
                            LocalDateTime.now().plusDays(30), // 데드라인
                            LocalDateTime.now(), // 시작일
                            LocalDateTime.now().plusDays(60), // 마감일
                            "개인", "정규직", 5000000L, 3, 2
                    ),
                    List.of(2L), // 강남구
                    List.of(2L), // 웹 개발
                    List.of(1L, 4L) // Java, React
            );

            projectService.create(
                    user2,
                    new PostWriteDto("디자인 리뉴얼", "Figma로 UI/UX 개선", true),
                    new ProjectWriteDto(
                            LocalDateTime.now().plusDays(15),
                            LocalDateTime.now(),
                            LocalDateTime.now().plusDays(45),
                            "기업", "계약직", 3000000L, 2, 1
                    ),
                    List.of(7L), // 해운대구
                    List.of(7L), // UI/UX 디자인
                    List.of(13L) // Figma
            );

            projectService.create(
                    user3,
                    new PostWriteDto("마케팅 캠페인", "SNS 마케팅 및 콘텐츠 제작", true),
                    new ProjectWriteDto(
                            LocalDateTime.now().plusDays(20),
                            LocalDateTime.now(),
                            LocalDateTime.now().plusDays(40),
                            "개인", "프리랜서", 2000000L, 1, 1
                    ),
                    List.of(13L), // 중구(대구)
                    List.of(12L), // 브랜드 마케팅
                    List.of(19L) // SNS 마케팅
            );
        }
    }
}
