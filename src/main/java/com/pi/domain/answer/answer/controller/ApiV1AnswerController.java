package com.pi.domain.answer.answer.controller;

import com.pi.domain.answer.answer.dto.AnswerCreateReqBody;
import com.pi.domain.answer.answer.dto.AnswerDto;
import com.pi.domain.answer.answer.dto.AnswerModifyReqBody;
import com.pi.domain.answer.answer.entity.Answer;
import com.pi.domain.answer.answer.service.AnswerService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
public class ApiV1AnswerController {
    private final AnswerService answerService;
    private final Rq rq;

    @PostMapping
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
    public RsData<AnswerDto> modifyAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerModifyReqBody reqBody
    ) {
        User actor = rq.getActor();

        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "답변을 찾을 수 없습니다."));

        answer.checkActorCanModify(actor);

        AnswerDto answerDto = answerService.modifyAnswer(answer, reqBody);
        return new RsData<>("200-1", "관리자가 %d번 답변을 수정했습니다.".formatted(id), answerDto);

    }

    @DeleteMapping("/{id}")
    public RsData<Void> deleteAnswer(@PathVariable Long id) {
        User actor = rq.getActor();

        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "답변을 찾을 수 없습니다."));

        answer.checkActorCanDelete(actor);

        answerService.delete(answer);
        return new RsData<>("200-1", "관리자가 %d번 답변을 삭제했습니다.".formatted(id));

    }
}
