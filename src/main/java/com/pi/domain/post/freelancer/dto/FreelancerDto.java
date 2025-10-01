package com.pi.domain.post.freelancer.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreelancerDto {
    private Long id;
    private String salary;
    private String period;
    private Long projectId;
}
