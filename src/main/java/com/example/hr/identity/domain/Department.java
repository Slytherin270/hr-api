package com.example.hr.identity.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("departments")
public class Department {
    @Id
    private UUID id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
    @Version
    private Long version;

    public Department(UUID id, String name, Instant createdAt, Instant updatedAt, UUID createdBy, UUID lastModifiedBy, Long version) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

    public static Department create(String name, UUID actor, Instant now) {
        return new Department(UUID.randomUUID(), name, now, now, actor, actor, null);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
