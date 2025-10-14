package com.pi.global.initData;

import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.review.review.service.ReviewService;
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
public class ProdReviewInitData {
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Bean
    ApplicationRunner prodReviewInitDataRunner() {
        return args -> {
            initReviews();
        };
    }

    @Transactional
    public void initReviews() {
        if (reviewService.count() > 0) {
            return;
        }

        User user1 = userRepository.findById(3L).orElseThrow();
        User user2 = userRepository.findById(4L).orElseThrow();

        reviewService.create(user1, 1L, 5, "회사 분위기가 매우 자유로워서 창의적으로 일할 수 있었습니다. 동료들과의 협업도 원활했고, 성장할 수 있는 기회가 많았습니다.");
        reviewService.create(user2, 2L, 3, "프로젝트 관리가 체계적이고, 업무 분장이 명확해서 효율적으로 일할 수 있었습니다. 다만, 의사소통 부분에서 약간의 아쉬움이 있었습니다.");
    }
}