package com.pi.domain.notification.notification.controller;

import com.pi.domain.notification.notification.dto.NotificationDto;
import com.pi.domain.notification.notification.service.NotificationService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    @Operation(summary = "알림 다건 조회")
    public List<NotificationDto> getItems() {
        User actor = rq.getActor();
        return notificationService.getItems(actor.getId());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "알림 삭제")
    public RsData<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return new RsData<>("200-1", "알림이 삭제되었습니다.");
    }

//
//    @GetMapping("/count-unread")
//    @Transactional(readOnly = true)
//    @Operation(summary = "안 읽은 알림 개수")
//    public Long getUnreadCount() {
//        User actor = rq.getActor();
//        return notificationService.countUnread(actor.getId());
//    }
//
//    @PatchMapping("/{id}/read")
//    @Transactional
//    @Operation(summary = "알림 단건 읽음 처리")
//    public NotificationDto readItem(@PathVariable Long id) {
//        User actor = rq.getActor();
//        return notificationService.read(id, actor.getId());
//    }
//
//    @PutMapping("/")
//    @Transactional
//    @Operation(summary = "알림 다건 읽음 처리")
//    public RsData<Void> readAll() {
//        User actor = rq.getActor();
//        notificationService.readAll(actor.getId());
//        return new RsData<>("200-1", "모든 알림이 읽음 처리되었습니다.");
//    }


}
