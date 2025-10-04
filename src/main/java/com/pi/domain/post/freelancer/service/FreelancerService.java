package com.pi.domain.post.freelancer.service;

import com.pi.domain.post.freelancer.dto.FreelancerReqBody;
import com.pi.domain.post.freelancer.dto.FreelancerResBody;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rsData.RsData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreelancerService {
    private final FreelancerRepository freelancerRepository;
    private final PostRepository postRepository;

    @Transactional
    public RsData<Freelancer> createFreelancer(User user, FreelancerReqBody reqBody) {
        Post post = new Post(
                user,
                false,
                reqBody.title(),
                reqBody.content()
        );
        postRepository.save(post);

        Freelancer freelancer = new Freelancer(post, reqBody.salary(), reqBody.period());
        freelancerRepository.save(freelancer);

        return new RsData<>("200-1", "프리랜서 게시글이 등록되었습니다.", freelancer);
    }

    @Transactional
    public RsData<Freelancer> updateFreelancer(Long freelancerId, FreelancerReqBody reqBody, User user) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프리랜서 게시글입니다."));

        Post post = freelancer.getPost();

        if(!post.getUser().equals(user)) {
            return new RsData<>("403-1", "본인 게시글만 수정할 수 있습니다.");
        }

        post.setTitle(reqBody.title());
        post.setContent(reqBody.content());
        postRepository.save(post);
        
        freelancer.setSalary(reqBody.salary());
        freelancer.setPeriod(reqBody.period());
        freelancerRepository.save(freelancer);

        return new RsData<>("200-2", "프리랜서 게시글이 수정되었습니다.", freelancer);
    }



}
