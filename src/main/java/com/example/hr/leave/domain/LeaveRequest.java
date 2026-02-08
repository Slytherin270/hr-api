package com.example.hr.leave.domain;

import com.example.hr.shared.domain.DomainException;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table("leave_requests")
public class LeaveRequest {
    @Id
    private UUID id;
    private UUID employeeId;
    private LeaveType type;
    private LeaveStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean morningHalfDay;
    private boolean afternoonHalfDay;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
    @Version
    private Long version;

    public LeaveRequest(UUID id, UUID employeeId, LeaveType type, LeaveStatus status, LocalDate startDate, LocalDate endDate,
                        boolean morningHalfDay, boolean afternoonHalfDay, Instant createdAt, Instant updatedAt, UUID createdBy,
                        UUID lastModifiedBy, Long version) {
        this.id = id;
        this.employeeId = employeeId;
        this.type = type;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.morningHalfDay = morningHalfDay;
        this.afternoonHalfDay = afternoonHalfDay;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

    public static LeaveRequest create(UUID employeeId, LeaveType type, LocalDate startDate, LocalDate endDate,
                                      boolean morningHalfDay, boolean afternoonHalfDay, UUID actor, Instant now) {
        if (morningHalfDay && afternoonHalfDay) {
            throw new DomainException("Cannot request both half-day segments");
        }
        return new LeaveRequest(UUID.randomUUID(), employeeId, type, LeaveStatus.PENDING, startDate, endDate,
                morningHalfDay, afternoonHalfDay, now, now, actor, actor, null);
    }

    public void approve(UUID actor, Instant now) {
        if (status != LeaveStatus.PENDING) {
            throw new DomainException("Leave request already processed");
        }
        this.status = LeaveStatus.APPROVED;
        this.updatedAt = now;
        this.lastModifiedBy = actor;
    }

    public void reject(UUID actor, Instant now) {
        if (status != LeaveStatus.PENDING) {
            throw new DomainException("Leave request already processed");
        }
        this.status = LeaveStatus.REJECTED;
        this.updatedAt = now;
        this.lastModifiedBy = actor;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public LeaveType getType() {
        return type;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isMorningHalfDay() {
        return morningHalfDay;
    }

    public boolean isAfternoonHalfDay() {
        return afternoonHalfDay;
    }
}
