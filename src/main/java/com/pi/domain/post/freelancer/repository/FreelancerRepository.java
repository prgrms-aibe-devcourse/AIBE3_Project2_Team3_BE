package com.pi.domain.post.freelancer.repository;

import com.pi.domain.post.freelancer.entity.Freelancer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {
    Page<Freelancer> findAllByPost_User(User user, Pageable pageable);
}
