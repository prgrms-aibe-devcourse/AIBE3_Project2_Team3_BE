package com.pi.domain.post.project.dto;

import com.pi.domain.post.post.dto.PostWriteDto;
import jakarta.validation.Valid;

import java.util.List;

public record ProjectWriteReqBody(
        @Valid
        PostWriteDto postWriteDto,
        @Valid
        ProjectWriteDto projectWriteDto,
        List<Long> regionIds,
        List<Long> categoryIds,
        List<Long> skillIds
) {
}
