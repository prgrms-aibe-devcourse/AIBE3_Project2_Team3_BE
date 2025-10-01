package com.pi.domain.question.question.controller;

import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.service.QuestionService;
import com.pi.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class ApiV1QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public RsData<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.findAll();
        return new RsData<>("200-1", null, questions);
    }

    @PostMapping
    public RsData<Question> createQuestion(@RequestBody Question question) {
        Question newQuestion = questionService.save(question);

        return new RsData<>(
                "200-1",
                "문의가 등록되었습니다.",
                newQuestion
        );
    }
}
