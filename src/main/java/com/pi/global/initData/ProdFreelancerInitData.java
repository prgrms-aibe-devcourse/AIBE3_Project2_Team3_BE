package com.pi.global.initData;

import com.pi.domain.post.freelancer.dto.FreelancerWriteDto;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ProdFreelancerInitData {
    private final FreelancerService freelancerService;
    private final UserRepository userRepository;

    @Transactional
    public void initFreelancers() {
        if (freelancerService.count() > 0) {
            return;
        }

        User user1 = userRepository.findById(3L).orElseThrow();
        User user2 = userRepository.findById(4L).orElseThrow();

        freelancerService.create(
                user1,
                new PostWriteDto("백엔드 개발 프리랜서 모집", "Spring Boot 경험자 우대", true),
                new FreelancerWriteDto(4000000L, 30L),
                List.of(2L), // 지역
                List.of(4L), // 카테고리
                List.of(2L)  // 스킬
        );

        freelancerService.create(
                user2,
                new PostWriteDto("디자인 프리랜서 모집", "UI/UX 디자인 경험자", true),
                new FreelancerWriteDto(3500000L, 20L),
                List.of(22L),
                List.of(8L),
                List.of(11L)
        );
    }
}