package com.pi.global.initData;

import com.pi.domain.answer.answer.entity.Answer;
import com.pi.domain.answer.answer.repository.AnswerRepository;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
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
public class ProdAnswerInitData {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Bean
    ApplicationRunner prodAnswerInitDataRunner() {
        return args -> {
            initAnswers();
        };
    }

    @Transactional
    public void initAnswers() {
        if (answerRepository.count() > 0) {
            return;
        }

        User admin = userRepository.findById(1L).orElseThrow();
        User system = userRepository.findById(2L).orElseThrow();

        Question q1 = questionRepository.findById(1L).orElseThrow();
        Question q2 = questionRepository.findById(2L).orElseThrow();
        Question q3 = questionRepository.findById(3L).orElseThrow();
        Question q4 = questionRepository.findById(4L).orElseThrow();
        Question q5 = questionRepository.findById(5L).orElseThrow();

        answerRepository.save(new Answer(
                "구인/구직 게시글은 상단 메뉴에서 등록 가능합니다. 회원가입 후 이용해 주세요.",
                admin, q1
        ));
        answerRepository.save(new Answer(
                "프리랜서 등록은 프로필 작성 후 인증 절차를 거치면 완료됩니다.",
                admin, q2
        ));
        answerRepository.save(new Answer(
                "프로젝트 완료 후 결제가 진행되며, 수수료는 정책에 따라 자동 부과됩니다.",
                system, q3
        ));
        answerRepository.save(new Answer(
                "구직자는 프로젝트 상세 페이지에서 지원 버튼을 눌러 정보를 입력하면 지원이 완료됩니다.",
                system, q4
        ));
        answerRepository.save(new Answer(
                "회원 탈퇴는 마이페이지에서 직접 신청하실 수 있습니다. 탈퇴 후 데이터는 복구되지 않습니다.",
                admin, q5
        ));

    }
}