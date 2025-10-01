package com.pi.global.initData;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.repository.OfferRepository;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FreelancerRepository freelancerRepository;
    private final OfferRepository offerRepository;

    @Bean
    ApplicationRunner notProdInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (userRepository.count() > 0) return;

        User user1 = userService.join("user1", "1234", "유저1", "user1@example.com");
        User user2 = userService.join("user2", "1234", "유저2", "user2@example.com");

        Post post1 = postRepository.save(new Post(user1, true, "만들어드립니다.", "만들어드립니다..."));
        Freelancer freelancer1 = freelancerRepository.save(
                Freelancer.builder()
                        .post(post1)
                        .salary("100")
                        .period("12")
                        .build()
        );

        offerRepository.save(new Offer(freelancer1, user2, OfferStatus.REQUESTED));
    }
}
