package com.pi.domain.answer.answer.controller;

import com.pi.domain.answer.answer.dto.AnswerCreateReqBody;
import com.pi.domain.answer.answer.dto.AnswerDto;
import com.pi.domain.answer.answer.dto.AnswerModifyReqBody;
import com.pi.domain.answer.answer.entity.Answer;
import com.pi.domain.answer.answer.service.AnswerService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
@Tag(name = "ApiV1AnswerController", description = "API 답변 컨트롤러")
public class ApiV1AnswerController {
    private final AnswerService answerService;
    private final Rq rq;

    @PostMapping
    @Operation(summary = "답변 등록")
    public RsData<AnswerDto> createAnswer(@Valid @RequestBody AnswerCreateReqBody reqBody) {
        User actor = rq.getActor();

        Answer.checkActorCanCreate(actor);

        AnswerDto answerDto = answerService.createAnswer(reqBody, actor);

        return new RsData<>(
                "201-1",
                "관리자가 답변을 등록했습니다.",
                answerDto);

    }

    @PutMapping("/{id}")
    @Operation(summary = "답변 수정")
    public RsData<AnswerDto> modifyAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerModifyReqBody reqBody
    ) {
        User actor = rq.getActor();

        Answer answer = answerService.findById(id);

        answer.checkActorCanModify(actor);

        AnswerDto answerDto = answerService.modifyAnswer(answer, reqBody);
        return new RsData<>("200-1", "관리자가 %d번 답변을 수정했습니다.".formatted(id), answerDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "답변 삭제")
    public RsData<Void> deleteAnswer(@PathVariable Long id) {
        User actor = rq.getActor();

        Answer answer = answerService.findById(id);

        answer.checkActorCanDelete(actor);

        answerService.delete(answer);
        return new RsData<>("200-1", "관리자가 %d번 답변을 삭제했습니다.".formatted(id));

    }
}
