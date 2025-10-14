package com.pi.domain.post.post.repository;

import com.pi.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Optional<Post> findByFreelancerIsNotNullAndId(Long id);

    Page<Post> findByFreelancerIsNotNull(Pageable pageable);

    Page<Post> findByFreelancerIsNotNullAndTitleContainingIgnoreCase(Pageable pageable, String title);

    Optional<Post> findByProjectIsNotNullAndId(Long id);

    Page<Post> findByProjectIsNotNull(Pageable pageable);

    Page<Post> findByProjectIsNotNullAndTitleContainingIgnoreCase(Pageable pageable, String searchKeyword);

    Optional<Post> findTopByOrderByIdDesc();

    List<Post> findByProjectIsNotNull();

    Page<Post> findByUserAndProjectIsNotNull(Long userId, Pageable pageable);
}
