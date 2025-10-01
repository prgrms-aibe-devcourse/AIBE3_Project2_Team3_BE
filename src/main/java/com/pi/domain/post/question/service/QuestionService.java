package com.pi.domain.post.question.service;

import com.pi.domain.post.question.entity.Question;
import com.pi.domain.post.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Question findById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }
}
