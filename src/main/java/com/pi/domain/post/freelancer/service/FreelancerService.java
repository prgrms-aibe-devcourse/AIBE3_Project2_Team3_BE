package com.pi.domain.post.freelancer.service;

import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerReqBody;
import com.pi.domain.post.freelancer.dto.FreelancerResBody;
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

    public FreelancerResBody create(FreelancerReqBody requestDto) {
        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        Freelancer freelancer = Freelancer.builder()
                .post(post)
                .salary(requestDto.salary())
                .period(requestDto.period())
                .build();

        Freelancer saved = freelancerRepository.save(freelancer);

        return new FreelancerResBody(
                new FreelancerDto(saved),
                "프리랜서가 성공적으로 등록되었습니다."
        );
    }

    public Freelancer findById(Long id) {
        return freelancerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
    }

    public FreelancerDto findDtoById(Long id) {
        return new FreelancerDto(findById(id));
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


