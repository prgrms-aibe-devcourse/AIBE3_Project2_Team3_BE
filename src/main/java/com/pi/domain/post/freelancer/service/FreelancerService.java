package com.pi.domain.post.freelancer.service;

import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreelancerService {

    private final FreelancerRepository freelancerRepository;
    private final PostRepository postRepository;

    public Freelancer save(Freelancer freelancer) {
        return freelancerRepository.save(freelancer);
    }

    public Freelancer findById(Long id) {
        return freelancerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
    }

    public List<Freelancer> findAll() {
        return freelancerRepository.findAll();
    }

    public void delete(Long id) {
        freelancerRepository.deleteById(id);
    }
}
