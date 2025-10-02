package com.pi.domain.post.freelancer.controller;

import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerReqBody;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/freelancers")
@RequiredArgsConstructor
public class ApiV1FreelancerController {
    private final FreelancerService freelancerService;

    @PostMapping
    public RsData<FreelancerDto> join(@RequestBody FreelancerReqBody req) {
        Freelancer saved = freelancerService.join(req.postId(), req.salary(), req.period());
        return new RsData<>(
                "201-1",
                "프리랜서 등록이 완료되었습니다.",
                new FreelancerDto(saved)
        );
    }

    @GetMapping("/{id}")
    public RsData<FreelancerDto> getById(@PathVariable Long id) {
        return freelancerService.findById(id)
                .map(f -> new RsData<>("200-1", "프리랜서 조회 성공", new FreelancerDto(f)))
                .orElse(new RsData<>("404-1", "프리랜서를 찾을 수 없습니다.", null));
    }

    @GetMapping
    public RsData<List<FreelancerDto>> getAll() {
        List<FreelancerDto> dtos = freelancerService.findAll().stream()
                .map(FreelancerDto::new)
                .toList();
        return new RsData<>("200-1", "프리랜서 전체 조회 성공", dtos);
    }
}

