package com.pi.domain.post.freelancer.dto;

import com.pi.domain.post.post.dto.PostModifyDto;
import jakarta.validation.Valid;

import java.util.List;

public record FreelancerModifyReqBody(
        @Valid
        PostModifyDto postModifyDto,
        @Valid
        FreelancerModifyDto freelancerModifyDto,
        List<Long> regionIds,
        List<Long> categoryIds,
        List<Long> skillIds
) {
}
