package com.pi.domain.post.freelancer.controller;

import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.service.FreelancerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/freelancers")
@RequiredArgsConstructor
public class ApiV1FreelancerController {

    private final FreelancerService freelancerService;

    @GetMapping
    public List<Freelancer> getAll() {
        return freelancerService.findAll();
    }

    @GetMapping("/{id}")
    public Freelancer getById(@PathVariable Long id) {
        return freelancerService.findById(id);
    }

    @PostMapping
    public Freelancer create(@RequestBody Freelancer freelancer) {
        return freelancerService.save(freelancer);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        freelancerService.delete(id);
    }
}
