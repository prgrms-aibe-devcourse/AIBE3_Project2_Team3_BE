package com.pi.domain.post.project.dto;

import java.time.LocalDateTime;


public record ProjectSearchDto(
        Long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String title,
        String content,
        boolean isViewed,
        Long salary,
        LocalDateTime deadlineDate
) {

}
