package com.pi.global.initData;

import com.pi.domain.question.question.dto.QuestionCreateReqBody;
import com.pi.domain.question.question.service.QuestionService;
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
public class ProdQuestionInitData {
    private final QuestionService questionService;
    private final UserRepository userRepository;

    @Bean
    ApplicationRunner prodQuestionInitDataRunner() {
        return args -> {
            initQuestions();
        };
    }

    @Transactional
    public void initQuestions() {
        if (questionService.count() > 0) {
            return;
        }

        User user1 = userRepository.findById(3L).orElseThrow();
        User user2 = userRepository.findById(4L).orElseThrow();
        User user3 = userRepository.findById(5L).orElseThrow();

        questionService.create(
                new QuestionCreateReqBody("사이트 이용 방법이 궁금해요.", "구인/구직 게시글을 등록하려면 어떻게 해야 하나요?"),
                user1
        );
        questionService.create(
                new QuestionCreateReqBody("프리랜서로 등록하려면 어떤 절차가 필요한가요?", "프리랜서 프로필 작성 및 인증 방법을 알려주세요."),
                user2
        );
        questionService.create(
                new QuestionCreateReqBody("결제 및 수수료 정책은 어떻게 되나요?", "프로젝트 완료 후 결제 과정과 수수료 부과 기준이 궁금합니다."),
                user3
        );
        questionService.create(
                new QuestionCreateReqBody("구직자가 프로젝트에 지원하는 방법은?", "구직자가 프로젝트에 지원할 때 필요한 정보와 절차를 설명해주세요."),
                user1
        );
        questionService.create(
                new QuestionCreateReqBody("회원 탈퇴는 어떻게 하나요?", "사이트에서 회원 탈퇴 절차를 안내해주세요."),
                user2
        );
    }
}
