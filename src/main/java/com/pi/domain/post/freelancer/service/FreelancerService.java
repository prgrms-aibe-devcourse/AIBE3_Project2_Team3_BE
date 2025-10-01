package com.pi.domain.post.freelancer.service;

import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerRequestDto;
import com.pi.domain.post.freelancer.dto.FreelancerResponseDto;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreelancerService {

    private final FreelancerRepository freelancerRepository;
    private final PostRepository postRepository;

    public FreelancerResponseDto create(FreelancerRequestDto requestDto) {
        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Freelancer freelancer = Freelancer.builder()
                .post(post)
                .salary(requestDto.salary())
                .period(requestDto.period())
                .build();

        Freelancer saved = freelancerRepository.save(freelancer);

        return new FreelancerResponseDto(new FreelancerDto(saved), "프리랜서가 등록되었습니다.");
    }

    public FreelancerDto findById(Long id) {
        Freelancer freelancer = freelancerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        return new FreelancerDto(freelancer);
    }

    public List<FreelancerDto> findAll() {
        return freelancerRepository.findAll()
                .stream()
                .map(FreelancerDto::new)
                .toList();
    }

    public void delete(Long id) {
        freelancerRepository.deleteById(id);
    }

}

