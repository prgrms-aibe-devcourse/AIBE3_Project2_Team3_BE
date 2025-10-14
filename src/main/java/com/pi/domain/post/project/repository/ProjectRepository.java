package com.pi.domain.post.project.repository;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.project.entity.Project;
import com.pi.domain.user.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
            SELECT pr.post FROM Project pr
            JOIN pr.post p
            LEFT JOIN p.postRegions prg
            LEFT JOIN p.postCategories pc
            LEFT JOIN p.postSkills ps
            WHERE (:keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%)
            
            AND (:regionIds IS NULL OR prg.region.id IN :regionIds)
            
            AND (:categoryIds IS NULL OR pc.category.id IN :categoryIds)
            
            AND (:skillIds IS NULL OR ps.skill.id IN :skillIds)
            
            AND (
                (:isOngoing = TRUE AND pr.deadlineDate > :now)
             OR (:isOngoing = FALSE AND pr.deadlineDate <= :now)
             OR (:isOngoing IS NULL)
            )
            ORDER BY p.id DESC
            """)
    List<Post> search(
            @Param("isOngoing") Boolean isOngoing,
            @Param("regionIds") List<Long> regionIds,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("skillIds") List<Long> skillIds,
            @Param("keyword") String keyword,
            @Param("now") LocalDateTime now
    );


    Page<Project> findByPost_User(User actor, Pageable pageable);
}
