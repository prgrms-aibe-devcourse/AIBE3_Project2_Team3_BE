package com.pi.domain.question.question.service;

import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Question findById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    public Question create(String title, String content, User user) {
        Question question = Question.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();

        return questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }
}
