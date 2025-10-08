package com.pi.domain.question.question.controller;

import com.pi.domain.offer.offer.dto.PagedResBody;
import com.pi.domain.question.question.dto.QuestionCreateReqBody;
import com.pi.domain.question.question.dto.QuestionDto;
import com.pi.domain.question.question.dto.QuestionModifyReqBody;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.service.QuestionService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class ApiV1QuestionController {
    private final QuestionService questionService;
    private final Rq rq;

    @GetMapping
    public RsData<PagedResBody<QuestionDto>> getAllQuestions(
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Question> questionPage = questionService.findAllWithAnswers(pageable);

        return new RsData<>(
                "200-1",
                "질문 목록을 조회했습니다.",
                new PagedResBody<>(
                        questionPage.getContent().stream()
                                .map(QuestionDto::new)
                                .toList(),
                        questionPage
                )
        );
    }

    @PostMapping
    public RsData<QuestionDto> createQuestion(@Valid @RequestBody QuestionCreateReqBody questionCreateDto) {
        User actor = rq.getActor();

        Question.checkActorCanCreate(actor);

        Question createdQuestion = questionService.create(questionCreateDto, actor);

        return new RsData<>(
                "201-1",
                "%d번 사용자가 질문을 등록했습니다.".formatted(actor.getId()),
                new QuestionDto(createdQuestion));
    }

    @DeleteMapping("/{id}")
    public RsData<Void> deleteQuestion(@PathVariable Long id) {
        User actor = rq.getActor();

        Question question = questionService.findById(id);

        question.checkActorCanDelete(actor);

        questionService.delete(id);

        return new RsData<>(
                "200-1",
                "%d번 사용자가 %d번 질문을 삭제했습니다.".formatted(actor.getId(), id)
        );
    }

    @PutMapping("/modify/{id}")
    public RsData<QuestionDto> modifyQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionModifyReqBody modifyDto
    ) {
        User actor = rq.getActor();

        Question question = questionService.findById(id);

        question.checkActorCanModify(actor);

        Question modifiedQuestion = questionService.modify(id, modifyDto);

        return new RsData<>(
                "200-1",
                "%d번 사용자가 %d번 질문을 수정했습니다.".formatted(actor.getId(), id),
                new QuestionDto(modifiedQuestion)
        );
    }
}
