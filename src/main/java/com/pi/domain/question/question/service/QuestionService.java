package com.pi.domain.question.question.service;

import com.pi.domain.question.question.dto.QuestionCreateReqBody;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    @Transactional
    public Question create(QuestionCreateReqBody dto, User user) {
        Question question = new Question(dto.title(), dto.content(), user);
        return questionRepository.save(question);
    }


    public void delete(Question question) {
        questionRepository.delete(question);
    }
}
