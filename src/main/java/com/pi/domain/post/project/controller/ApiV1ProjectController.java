package com.pi.domain.post.project.controller;

import com.pi.domain.post.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ApiV1ProjectController {
    private final ProjectService projectService;
}
