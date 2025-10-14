package com.pi.global.initData;

import com.pi.domain.offer.offer.service.OfferService;
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
public class ProdOfferInitData {
    private final OfferService offerService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Bean
    ApplicationRunner prodOfferInitDataRunner() {
        return args -> {
            initOffers();
        };
    }

    @Transactional
    public void initOffers() {
        if (offerService.count() > 0) {
            return;
        }

        User user5 = userRepository.findById(4L).orElseThrow();
        Post post1 = postRepository.findById(4L).orElseThrow();
        offerService.create(post1, user5, 1);

        User user4 = userRepository.findById(5L).orElseThrow();
        Post post2 = postRepository.findById(5L).orElseThrow();
        offerService.create(post2, user4, 2);

    }
}
