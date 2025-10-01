package com.pi.domain.post.freelancer.dto;

import com.pi.domain.post.freelancer.entity.Freelancer;

import java.time.LocalDateTime;

public record FreelancerDto (
        Long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String title,
        String content,
        boolean isViewed,
        String salary,
        String period
){
    public FreelancerDto(Freelancer freelancer) {
        this(
                freelancer.getId(),
                freelancer.getPost().getCreatedDate(),
                freelancer.getPost().getModifiedDate(),
                freelancer.getPost().getTitle(),
                freelancer.getPost().getContent(),
                freelancer.getPost().isViewed(),
                freelancer.getSalary(),
                freelancer.getPeriod()
        );
    }
}
