package com.pi.domain.post.project.dto;

import com.pi.domain.post.post.dto.PostModifyDto;
import jakarta.validation.Valid;

import java.util.List;

public record ProjectModifyReqBody(
        @Valid
        PostModifyDto post,
        @Valid
        ProjectModifyDto project,
        List<Long> regionIds,
        List<Long> categoryIds,
        List<Long> skillIds
) {
}
