package com.pi.domain.question.question.service;

import com.pi.domain.question.question.dto.QuestionCreateReqBody;
import com.pi.domain.question.question.dto.QuestionModifyReqBody;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Page<Question> getPage(Pageable pageable, String searchKeyword) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return questionRepository.findAll(pageable);
        }
        return questionRepository.findByTitleContainingIgnoreCase(searchKeyword, pageable);
    }

    @Transactional
    public Question create(QuestionCreateReqBody dto, User user) {
        Question question = new Question(dto.title(), dto.content(), user);
        return questionRepository.save(question);
    }

    public Question findById(Long id) {
        return questionRepository.findById(id).get();
    }

    @Transactional
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

    public Page<Question> findAllWithAnswersByUserId(Long userId, Pageable pageable) {
        List<Question> questions = questionRepository.findAllWithAnswersByUserId(userId, pageable);
        long total = questions.size();
        return new PageImpl<>(questions, pageable, total);
    }
}

