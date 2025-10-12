package com.pi.domain.notification.notification.controller;

import com.pi.domain.notification.notification.dto.NotificationDto;
import com.pi.domain.notification.notification.service.NotificationService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.rsData.RsData;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "ApiV1NotificationController", description = "API 알림 컨트롤러")
public class ApiV1NotificationController {
    private final NotificationService notificationService;
    private final Rq rq;

    @GetMapping
    @Operation(summary = "JSON 알림 다건 조회")
    public PagePayload<NotificationDto> getItems(@ParameterObject @PageableDefault(size = 6, direction = Sort.Direction.DESC) Pageable pageable) {
        User actor = rq.getActor();
        Page<NotificationDto> dtoPage = notificationService.getPage(actor.getId(), pageable).map(NotificationDto::new);
        return Ut.pageMapper.of(dtoPage);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "JSON 알림 삭제")
    public RsData<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return new RsData<>("200-1", "알림이 삭제되었습니다.");
    }


}
