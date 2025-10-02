package com.pi.domain.post.freelancer.service;

import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FreelancerService {
    private final FreelancerRepository freelancerRepository;
    private final PostRepository postRepository;

    public Freelancer join(Long postId, String salary, String period) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Freelancer freelancer = Freelancer.builder()
                .post(post)
                .salary(salary)
                .period(period)
                .build();

        return freelancerRepository.save(freelancer);
    }

    public Optional<Freelancer> findById(Long id) {
        return freelancerRepository.findById(id);
    }

    public List<Freelancer> findAll() {
        return freelancerRepository.findAll();
    }
}
