package com.pi.domain.post.freelancer.controller;

import com.pi.domain.post.freelancer.service.FreelancerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/freelancers")
@RequiredArgsConstructor
public class ApiV1FreelancerController {
    private final FreelancerService freelancerService;


}

