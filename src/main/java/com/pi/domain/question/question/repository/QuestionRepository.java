package com.pi.domain.question.question.repository;

import com.pi.domain.question.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
