package com.pi.domain.question.question.repository;

import com.pi.domain.question.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT DISTINCT q FROM Question q " +
            "LEFT JOIN FETCH q.answers a " +
            "LEFT JOIN FETCH q.user qu " +
            "LEFT JOIN FETCH a.user au")
    Page<Question> findAllWithAnswers(Pageable pageable);

}
