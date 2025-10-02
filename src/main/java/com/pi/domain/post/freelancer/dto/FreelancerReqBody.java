package com.pi.domain.post.freelancer.dto;

public record FreelancerReqBody(
        Long postId,
        String salary,
        String period
) {}
