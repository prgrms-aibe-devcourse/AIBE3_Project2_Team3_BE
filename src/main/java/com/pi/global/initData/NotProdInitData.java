package com.pi.global.initData;

import com.pi.domain.answer.answer.dto.AnswerCreateReqBody;
import com.pi.domain.answer.answer.service.AnswerService;
import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.domain.offer.offer.service.OfferService;
import com.pi.domain.post.freelancer.dto.FreelancerWriteDto;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.service.PostService;
import com.pi.domain.post.project.dto.ProjectWriteDto;
import com.pi.domain.post.project.service.ProjectService;
import com.pi.domain.question.question.dto.QuestionCreateReqBody;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.service.QuestionService;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.region.region.repository.RegionRepository;
import com.pi.domain.region.region.service.RegionService;
import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.skill.skill.repository.SkillRepository;
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

import java.time.LocalDateTime;
import java.util.List;

@Profile("!prod")
@RequiredArgsConstructor
@Configuration
public class NotProdInitData {
    private final UserService userService;
    private final OfferService offerService;
    private final PostService postService;
    private final FreelancerService freelancerService;
    private final ProjectService projectService;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final RegionService regionService;
    @Autowired
    @Lazy
    private NotProdInitData self;

    @Bean
    ApplicationRunner notProdInitDataApplicationRunner() {
        return args -> {
            self.work1();
            self.work5();
            self.work2();
            self.work3();
            self.work4();
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
        if (postService.count() > 0) return;
        User user1 = userService.findByUsername("user1").get();
        List<Long> singleIdList = List.of(1L);
        Post post1 = freelancerService.create(user1, new PostWriteDto("프리랜서", "만들어드립니다.", true), new FreelancerWriteDto(100L, 12L), null, null, null);
        Post post2 = projectService.create(user1, new PostWriteDto("프로젝트", "만들어드립니다.", true), new ProjectWriteDto(LocalDateTime.now().plusDays(7), LocalDateTime.now(), LocalDateTime.now().plusMonths(1), "실무", "내용", 100L, 10, 1), singleIdList, singleIdList, singleIdList);
    }

    @Transactional
    public void work3() {
        if (offerService.count() > 0) return;
        User user2 = userService.findByUsername("user2").get();
        Post post1 = freelancerService.findById(1L);
        Freelancer freelancer1 = post1.getFreelancer();

        offerService.create(freelancer1, user2);
    }

    @Transactional
    public boolean work4() {
        if (answerService.count() > 0) return false;

        Question question = questionService.create(
                new QuestionCreateReqBody("테스트 질문", "일반 사용자 테스트 작성 내용."),
                userRepository.findByUsername("user1").get()
        );

        answerService.createAnswer(
                new AnswerCreateReqBody("관리자 테스트 답변.", question.getId()),
                userRepository.findByUsername("admin").get()
        );

        return true;
    }


    public void work5() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("웹 개발"));
            categoryRepository.save(new Category("디자인"));
        }
        if (regionRepository.count() == 0) {
            regionRepository.save(new Region("서울"));
            regionRepository.save(new Region("경기"));
        }
        if (skillRepository.count() == 0) {
            skillRepository.save(new Skill("Java"));
            skillRepository.save(new Skill("React"));
        }
    }
}
