package com.pi.global.initData;

import com.pi.domain.offer.offer.repository.OfferRepository;
import com.pi.domain.offer.offer.service.OfferService;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Profile("!prod")
@RequiredArgsConstructor
@Configuration
public class NotProdInitData {
    @Autowired
    @Lazy
    private NotProdInitData self;

    private final UserService userService;
    private final PostRepository postRepository;
    private final FreelancerRepository freelancerRepository;
    private final OfferRepository offerRepository;
    private final OfferService offerService;

    @Bean
    ApplicationRunner notProdInitDataApplicationRunner() {
        return args -> {
            self.work1();
            self.work2();
        };
    }

    @Transactional
    public void work1() {
        if (userService.count() > 0) return;

        User userSystem = userService.join("system", "1234", "시스템", "system@test.com");
        User userAdmin = userService.join("admin", "1234", "관리자", "admin@test.com");
        User user1 = userService.join("user1", "1234", "유저1", "user1@test.com");
        User user2 = userService.join("user2", "1234", "유저2", "user2@test.com");
        User user3 = userService.join("user3", "1234", "유저3", "user3@test.com");
    }

    @Transactional
    public void work2() {
        if (offerService.count() > 0) return;
        User user1 = userService.findByUsername("user1").get();
        User user2 = userService.findByUsername("user2").get();
        Post post1 = postRepository.save(new Post(user1, true, "만들어드립니다.", "만들어드립니다..."));
        Freelancer freelancer1 = freelancerRepository.save(
                Freelancer.builder()
                        .post(post1)
                        .salary("100")
                        .period("12")
                        .build()
        );

        offerService.create(freelancer1, user2);
    }
}
