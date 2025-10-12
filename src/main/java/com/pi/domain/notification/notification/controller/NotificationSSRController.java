package com.pi.domain.notification.notification.controller;

import com.pi.domain.notification.notification.dto.NotificationDto;
import com.pi.domain.notification.notification.service.NotificationService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "NotificationSSRController", description = "SSR 알림 뷰 컨트롤러")
public class NotificationSSRController {
    private final NotificationService notificationService;
    private final Rq rq;

    @GetMapping
    @Operation(summary = "알림 다건 조회 페이지")
    public String getItems(Model model) {
        User actor = rq.getActor();
        List<NotificationDto> notifications = notificationService.getItems(actor.getId());
        model.addAttribute("notifications", notifications);

        return "notification/list";
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "알림 삭제")
    public String delete(@PathVariable Long id) {
        notificationService.delete(id);
        return "redirect:/notifications";
    }
//    SSE를 이용할지 몰라서 일단 주석처리
//    @GetMapping(value = "/subscribe", produces = "text/event-stream")
//    @Operation(summary = "알림 구독")
//    public SseEmitter subscribe() {
//        User actor = rq.getActor();
//        return notificationService.subscribe(actor.getId());
//    }
//
//    @PostMapping("/send")
//    @Operation(summary = "알림 저장 및 전송 -테스트용")
//    public RsData<Void> send(@RequestParam String message) {
//        User actor = rq.getActor();
//        notificationService.createAndNotify(actor.getId(), message);
//
//        return new RsData<>("200-1", "알림이 전송되었습니다.");
//    }
}
