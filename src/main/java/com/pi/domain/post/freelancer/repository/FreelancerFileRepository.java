package com.pi.domain.post.freelancer.repository;

import com.pi.domain.post.freelancer.entity.FreelancerFile;
import com.pi.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerFileRepository extends JpaRepository<FreelancerFile, Long> {
    List<FreelancerFile> findByPost(Post post);
    void deleteByPost(Post post);
}
