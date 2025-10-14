package com.pi.global.initData;

import com.pi.domain.answer.answer.dto.AnswerCreateReqBody;
import com.pi.domain.answer.answer.service.AnswerService;
import com.pi.domain.category.category.dto.CategoryCreateReqBody;
import com.pi.domain.category.category.service.CategoryService;
import com.pi.domain.notification.notification.entity.Notification;
import com.pi.domain.notification.notification.repository.NotificationRepository;
import com.pi.domain.notification.notification.service.NotificationService;
import com.pi.domain.offer.offer.entity.Offer;
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
import com.pi.domain.region.region.dto.RegionCreateReqBody;
import com.pi.domain.region.region.service.RegionService;
import com.pi.domain.skill.skill.dto.SkillCreateReqBody;
import com.pi.domain.skill.skill.service.SkillService;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.entity.UserRole;
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
    private final CategoryService categoryService;
    private final RegionService regionService;
    private final SkillService skillService;
    private final UserRepository userRepository;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final NotificationRepository notificationRepository;
    private final NotificationService notficationService;
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
            self.work6();
        };
    }

    @Transactional
    public void work1() {
        if (userService.count() > 0) return;

        User userSystem = userService.join("system", "1234", "시스템", "system@test.com", UserRole.ROLE_ADMIN);
        User userAdmin = userService.join("admin", "1234", "관리자", "admin@test.com", UserRole.ROLE_ADMIN);
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
        offerService.create(freelancer1.getPost(), user2, 2);
    }

    @Transactional
    public void work4() {
        if (answerService.count() > 0) return;

        Question question = questionService.create(
                new QuestionCreateReqBody("테스트 질문", "일반 사용자 테스트 작성 내용."),
                userRepository.findByUsername("user1").get()
        );

        answerService.createAnswer(
                new AnswerCreateReqBody("관리자 테스트 답변.", question.getId()),
                userRepository.findByUsername("admin").get()
        );
    }


    public void work5() {
        if (categoryService.count() == 0) {
            categoryService.create(new CategoryCreateReqBody("웹 개발", null));
            categoryService.create(new CategoryCreateReqBody("디자인", null));
            categoryService.create(new CategoryCreateReqBody("프론트엔드", 1L));
            categoryService.create(new CategoryCreateReqBody("백엔드", 1L));
        }
        if (regionService.count() == 0) {
            regionService.create(new RegionCreateReqBody("서울", null));
            regionService.create(new RegionCreateReqBody("경기", null));
            regionService.create(new RegionCreateReqBody("강남구", 1L));
            regionService.create(new RegionCreateReqBody("동대문구", 1L));
        }
        if (skillService.count() == 0) {
            skillService.create(new SkillCreateReqBody("Java"));
            skillService.create(new SkillCreateReqBody("React"));
        }
    }

    public void work6() {
        if (notificationRepository.count() == 0) {
            User user1 = userService.findByUsername("user1").get();
            Offer offer = offerService.findById(1L);
            Notification n = new Notification(user1, "내용1");
            n.addOffer(offer);
            notificationRepository.save(n);
        }
    }
}