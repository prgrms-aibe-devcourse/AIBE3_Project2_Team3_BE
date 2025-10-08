package com.pi.domain.answer.answer.service;

import com.pi.domain.answer.answer.dto.AnswerCreateReqBody;
import com.pi.domain.answer.answer.dto.AnswerDto;
import com.pi.domain.answer.answer.dto.AnswerModifyReqBody;
import com.pi.domain.answer.answer.entity.Answer;
import com.pi.domain.answer.answer.repository.AnswerRepository;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public AnswerDto createAnswer(AnswerCreateReqBody reqBody, User actor) {
        Question question = questionRepository.findById(reqBody.questionId()).get();

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

    public Answer findById(Long id) {
        return answerRepository.findById(id).get();
    }

    @Transactional
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    public long count() {
        return answerRepository.count();
    }

}

