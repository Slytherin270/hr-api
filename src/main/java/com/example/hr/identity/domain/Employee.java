package com.example.hr.identity.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("employees")
public class Employee {
    @Id
    private UUID id;
    private String email;
    private String fullName;
    private String passwordHash;
    private Role role;
    private UUID departmentId;
    private boolean active;
    private boolean deleted;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
    @Version
    private Long version;

    public Employee(UUID id, String email, String fullName, String passwordHash, Role role, UUID departmentId,
                    boolean active, boolean deleted, Instant createdAt, Instant updatedAt, UUID createdBy, UUID lastModifiedBy, Long version) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.role = role;
        this.departmentId = departmentId;
        this.active = active;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

    public static Employee create(String email, String fullName, String passwordHash, Role role, UUID departmentId, UUID actor, Instant now) {
        return new Employee(UUID.randomUUID(), email, fullName, passwordHash, role, departmentId,
                true, false, now, now, actor, actor, null);
    }

    public void updateProfile(String fullName, UUID departmentId, UUID actor, Instant now) {
        this.fullName = fullName;
        this.departmentId = departmentId;
        this.updatedAt = now;
        this.lastModifiedBy = actor;
    }

    public void softDelete(UUID actor, Instant now) {
        this.deleted = true;
        this.updatedAt = now;
        this.lastModifiedBy = actor;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public UUID getLastModifiedBy() {
        return lastModifiedBy;
    }
}
