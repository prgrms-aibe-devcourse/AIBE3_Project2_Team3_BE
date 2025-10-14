package com.pi.global.initData;

import com.pi.domain.application.application.service.ApplicationService;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class ProdApplicationInitData {
    private final ApplicationService applicationService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Bean
    ApplicationRunner prodApplicationInitDataRunner() {
        return args -> {
            initApplications();
        };
    }

    @Transactional
    public void initApplications() {
        if (applicationService.count() > 0) {
            return;
        }

        Post post1 = postRepository.findById(1L).orElseThrow();
        Post post2 = postRepository.findById(2L).orElseThrow();
        Post post3 = postRepository.findById(3L).orElseThrow();

        User user1 = userRepository.findById(3L).orElseThrow();
        User user2 = userRepository.findById(4L).orElseThrow();
        User user3 = userRepository.findById(5L).orElseThrow();

        applicationService.create(post1, user1,
                new com.pi.domain.application.application.dto.ApplicationWriteReqBody(
                        post1.getId(), "웹 개발 지원합니다.", 5000000L, 30
                ), null);

        applicationService.create(post2, user2,
                new com.pi.domain.application.application.dto.ApplicationWriteReqBody(
                        post2.getId(), "디자인 리뉴얼 경험 있습니다.", 3000000L, 20
                ), null);

        applicationService.create(post3, user3,
                new com.pi.domain.application.application.dto.ApplicationWriteReqBody(
                        post3.getId(), "마케팅 캠페인 참여 희망합니다.", 2000000L, 15
                ), null);
    }
}
