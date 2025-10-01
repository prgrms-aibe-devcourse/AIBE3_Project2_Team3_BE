package com.pi.domain.post.freelancer.controller;

import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerRequestDto;
import com.pi.domain.post.freelancer.dto.FreelancerResponseDto;
import com.pi.domain.post.freelancer.service.FreelancerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/freelancers")
@RequiredArgsConstructor
public class ApiV1FreelancerController {

    private final FreelancerService freelancerService;

    @GetMapping
    public List<FreelancerDto> getAll() {
        return freelancerService.findAll();
    }

    @GetMapping("/{id}")
    public FreelancerDto getById(@PathVariable Long id) {
        return freelancerService.findById(id);
    }

    @PostMapping
    public FreelancerResponseDto create(@Valid @RequestBody FreelancerRequestDto requestDto) {
        return freelancerService.create(requestDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        freelancerService.delete(id);
    }
}
