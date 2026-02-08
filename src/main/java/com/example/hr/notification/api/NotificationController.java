package com.example.hr.notification.api;

import com.example.hr.notification.domain.NotificationMessage;
import com.example.hr.notification.infra.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationRepository repository;

    public NotificationController(NotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<NotificationResponse>> list(Pageable pageable) {
        Page<NotificationMessage> messages = repository.findAll(pageable);
        return ResponseEntity.ok(messages.map(NotificationResponse::from));
    }

    public record NotificationResponse(UUID id, String channel, String payload) {
        public static NotificationResponse from(NotificationMessage message) {
            return new NotificationResponse(message.getId(), message.getChannel(), message.getPayload());
        }
    }
}
