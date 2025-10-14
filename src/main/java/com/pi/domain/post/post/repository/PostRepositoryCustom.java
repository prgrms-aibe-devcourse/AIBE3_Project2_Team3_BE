package com.pi.domain.post.post.repository;

import com.pi.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<Post> searchFreelancers(Pageable pageable,
                                 Long categoryId,
                                 Long regionId,
                                 List<Long> skillIds,
                                 String title,
                                 Long minSalary,
                                 Long maxSalary);
}
