package com.pi.domain.question.question.controller;

import com.pi.domain.question.question.dto.QuestionCreateDto;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.service.QuestionService;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class ApiV1QuestionController {
    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping
    public RsData<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.findAll();
        return new RsData<>("200-SUCCESS", null, questions);

    }

    @PostMapping
    public RsData<Question> createQuestion(
            @RequestBody QuestionCreateDto questionCreateDto,
            Principal principal
    ) {
        try {
            User currentUser = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            Question newQuestion = questionService.create(
                    questionCreateDto.title(),
                    questionCreateDto.content(),
                    currentUser
            );

            return new RsData<>(
                    "201-CREATED",
                    "질문이 성공적으로 등록되었습니다.",
                    newQuestion
            );
        } catch (IllegalArgumentException e) {
            return new RsData<>(
                    "404-NOT_FOUND",
                    e.getMessage()
            );
        } catch (Exception e) {
            return new RsData<>(
                    "400-BAD_REQUEST",
                    "질문 등록 중 오류가 발생했습니다: " + e.getMessage()
            );
        }
    }

}
