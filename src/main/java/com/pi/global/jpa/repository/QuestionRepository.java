package com.pi.global.jpa.repository;

import com.pi.global.jpa.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
