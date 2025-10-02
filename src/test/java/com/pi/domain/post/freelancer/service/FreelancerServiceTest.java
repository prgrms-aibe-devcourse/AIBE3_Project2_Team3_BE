package com.pi.domain.post.freelancer.service;

import com.pi.domain.post.freelancer.dto.FreelancerReqBody;
import com.pi.domain.post.freelancer.dto.FreelancerResBody;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FreelancerServiceTest {

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private FreelancerService freelancerService;

    public FreelancerServiceTest() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    void 프리랜서_생성_성공() {
        // given
        FreelancerReqBody requestDto = new FreelancerReqBody(
                1L, "5000만원", "6개월"
        );

        Post post = new Post();
        Freelancer freelancer = Freelancer.builder()
                .post(post)
                .salary("5000만원")
                .period("6개월")
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(freelancerRepository.save(freelancer)).thenReturn(freelancer);

        // when
        FreelancerResBody response = freelancerService.create(requestDto);

        // then
        assertThat(response.freelancerDto().salary()).isEqualTo("5000만원");
        assertThat(response.message()).isEqualTo("프리랜서가 성공적으로 등록되었습니다.");
    }
}
