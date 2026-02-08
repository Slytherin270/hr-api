package com.example.hr.notification.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("notifications")
public class NotificationMessage {
    @Id
    private UUID id;
    private String channel;
    private String payload;
    private Instant createdAt;
    private UUID createdBy;
    @Version
    private Long version;

    public NotificationMessage(UUID id, String channel, String payload, Instant createdAt, UUID createdBy, Long version) {
        this.id = id;
        this.channel = channel;
        this.payload = payload;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.version = version;
    }

    public static NotificationMessage of(String channel, String payload, UUID actor, Instant now) {
        return new NotificationMessage(UUID.randomUUID(), channel, payload, now, actor, null);
    }

    public UUID getId() {
        return id;
    }

    public String getChannel() {
        return channel;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
