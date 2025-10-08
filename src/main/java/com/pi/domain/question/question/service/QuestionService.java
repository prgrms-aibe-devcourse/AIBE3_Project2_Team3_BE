package com.pi.domain.question.question.service;

import com.pi.domain.question.question.dto.QuestionCreateReqBody;
import com.pi.domain.question.question.dto.QuestionModifyReqBody;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Page<Question> findAllWithAnswers(Pageable pageable) {
        return questionRepository.findAllWithAnswers(pageable);
    }


    @Transactional
    public Question create(QuestionCreateReqBody dto, User user) {
        Question question = new Question(dto.title(), dto.content(), user);
        return questionRepository.save(question);
    }

    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "해당 질문을 찾을 수 없습니다."));
    }

    public void delete(Long id) {
        Question question = findById(id);
        questionRepository.delete(question);
    }

    @Transactional
    public Question modify(Long id, QuestionModifyReqBody dto) {
        Question question = findById(id);
        question.modify(dto.title(), dto.content());
        return questionRepository.save(question);
    }
}

