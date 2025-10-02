package com.pi.domain.question.question.controller;

import com.pi.domain.question.question.dto.QuestionCreateReqBody;
import com.pi.domain.question.question.dto.QuestionDto;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.service.QuestionService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class ApiV1QuestionController {
    private final QuestionService questionService;
    private final Rq rq;

    @GetMapping
    public RsData<List<QuestionDto>> getAllQuestions() {
        List<Question> questions = questionService.findAll();
        List<QuestionDto> questionDtos = questions.stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
        return new RsData<>("200-1", "질문 목록을 조회했습니다.", questionDtos);
    }

    @PostMapping
    public RsData<Question> createQuestion(@Valid @RequestBody QuestionCreateReqBody questionCreateDto) {
        User actor = rq.getActor();

        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        Question createdQuestion = questionService.create(questionCreateDto, actor);
        return new RsData<>(
                "201-1",
                "질문이 생성되었습니다.",
                createdQuestion
        );


    }


}
