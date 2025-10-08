package com.pi.domain.answer.answer.service;

import com.pi.domain.answer.answer.dto.AnswerCreateReqBody;
import com.pi.domain.answer.answer.dto.AnswerDto;
import com.pi.domain.answer.answer.dto.AnswerModifyReqBody;
import com.pi.domain.answer.answer.entity.Answer;
import com.pi.domain.answer.answer.repository.AnswerRepository;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public AnswerDto createAnswer(AnswerCreateReqBody reqBody, User actor) {
        Question question = questionRepository.findById(reqBody.questionId())
                .orElseThrow(() -> new ServiceException(
                        "404-1",
                        "데이터를 찾을 수 없습니다."
                ));

        Answer answer = new Answer(
                reqBody.content(),
                actor,
                question
        );

        answerRepository.save(answer);
        return new AnswerDto(answer);
    }

    @Transactional
    public AnswerDto modifyAnswer(Answer answer, AnswerModifyReqBody reqBody) {
        answer.setContent(reqBody.content());
        return new AnswerDto(answer);
    }

    public Optional<Answer> findById(Long id) {
        return answerRepository.findById(id);
    }

    @Transactional
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    public long count() {
        return answerRepository.count();
    }

}

