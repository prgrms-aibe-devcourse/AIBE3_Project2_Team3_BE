package com.pi.domain.post.question.repository;

import com.pi.domain.post.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
