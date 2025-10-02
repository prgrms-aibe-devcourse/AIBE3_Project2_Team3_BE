package com.pi.domain.question.question.controller;

import com.pi.domain.question.question.dto.QuestionCreateReqBody;
import com.pi.domain.question.question.dto.QuestionDto;
import com.pi.domain.question.question.dto.QuestionModifyReqBody;
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

    @PostMapping("/create")
    public RsData<QuestionDto> createQuestion(@Valid @RequestBody QuestionCreateReqBody questionCreateDto) {
        User actor = rq.getActor();
        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        Question createdQuestion = questionService.create(questionCreateDto, actor);
        return new RsData<>("201-1", "질문이 생성되었습니다.", new QuestionDto(createdQuestion));
    }

    @DeleteMapping("/delete/{id}")
    public RsData<Void> deleteQuestion(@PathVariable Long id) {
        User actor = rq.getActor();
        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        Question question = questionService.findById(id);
        question.checkActorCanDelete(actor);

        questionService.delete(id);
        return new RsData<>("200-1", "질문이 삭제되었습니다.");
    }

    @PutMapping("/modify/{id}")
    public RsData<QuestionDto> modifyQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionModifyReqBody modifyDto
    ) {
        User actor = rq.getActor();
        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        Question question = questionService.findById(id);
        question.checkActorCanModify(actor);

        Question modifiedQuestion = questionService.modify(id, modifyDto);
        return new RsData<>("200-1", "질문이 수정되었습니다.", new QuestionDto(modifiedQuestion));
    }

}
