package com.pi.domain.notification.notification.service;

import com.pi.domain.notification.notification.dto.NotificationDto;
import com.pi.domain.notification.notification.entity.Notification;
import com.pi.domain.notification.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public Notification findById(Long id) {
        return notificationRepository.findById(id).get();
    }


    @Transactional(readOnly = true)
    public List<NotificationDto> getItems(long userId) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(notification -> new NotificationDto((Notification) notification))
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Notification notification = findById(id);

        notificationRepository.delete(notification);
    }


    public SseEmitter subscribe(long userId) {
        SseEmitter emitter = new SseEmitter(60L * 1000L);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        sendToClient(userId, "연결되었습니다.");
        return emitter;
    }

    public void sendToClient(long userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }

    public void createAndNotify(Long userId, String content) {
        Notification notification = new Notification();
        notification.setContent(content);
        notificationRepository.save(notification);

        sendToClient(userId, content);
    }


}
