package com.pi.domain.notification.notification.controller;

import com.pi.domain.notification.notification.dto.NotificationDto;
import com.pi.domain.notification.notification.service.NotificationService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "ApiV1NotificationController", description = "API 알림 컨트롤러")
public class ApiV1NotificationController {
    private final NotificationService notificationService;
    private final Rq rq;

    @GetMapping
    @Transactional
    @Operation(summary = "알림 다건 조회")
    public List<NotificationDto> getItems() {
        User actor = rq.getActor();
        return notificationService.getItems(actor);
    }

    @PatchMapping("/{id}/read")
    @Transactional
    @Operation(summary = "알림 단건 읽음 처리")
    public NotificationDto readItem(@PathVariable Long id) {
        User actor = rq.getActor();
        return notificationService.read(actor, id);
    }

    @PutMapping("/")
    @Transactional
    @Operation(summary = "알림 다건 읽음 처리")
    public void readAll() {

        notificationService.readAll();
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "알림 삭제")
    public void delete(@PathVariable Long id) {
        notificationService.delete(id);
    }

    @GetMapping("/count-unread")
    @Transactional
    @Operation(summary = "안 읽은 알림 개수")
    public Long count() {
        return notificationService.countUnread();
    }

    @PostMapping("/")
    @Transactional
    @Operation(summary = "알림 생성")
    public void create(@RequestBody NotificationDto notificationDto) {
        notificationService.create(notificationDto);
    }


}
