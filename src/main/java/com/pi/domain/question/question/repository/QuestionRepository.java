package com.pi.domain.question.question.repository;

import com.pi.domain.question.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("""
        SELECT q FROM Question q
        """)
    Page<Question> findAll(Pageable pageable);

    @Query("""
        SELECT DISTINCT q FROM Question q
        LEFT JOIN FETCH q.answers a
        LEFT JOIN FETCH q.user qu
        LEFT JOIN FETCH a.user au
        WHERE q.id IN :ids
        """)
    List<Question> findAllWithAnswersByIds(List<Long> ids);

    @Query("""
        SELECT DISTINCT q FROM Question q
        LEFT JOIN FETCH q.answers a
        LEFT JOIN FETCH q.user qu
        LEFT JOIN FETCH a.user au
        WHERE q.user.id = :userId
        """)
    List<Question> findAllWithAnswersByUserId(Long userId, Pageable pageable);
}
