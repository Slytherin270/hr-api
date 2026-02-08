package com.example.hr.attendance.domain;

import com.example.hr.shared.domain.DomainException;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Table("attendance_records")
public class AttendanceRecord {
    @Id
    private UUID id;
    private UUID employeeId;
    private LocalDate day;
    private Instant checkInTime;
    private Instant checkOutTime;
    private AttendanceStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
    @Version
    private Long version;

    public AttendanceRecord(UUID id, UUID employeeId, LocalDate day, Instant checkInTime, Instant checkOutTime,
                            AttendanceStatus status, Instant createdAt, Instant updatedAt, UUID createdBy, UUID lastModifiedBy, Long version) {
        this.id = id;
        this.employeeId = employeeId;
        this.day = day;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

    public static AttendanceRecord checkIn(UUID employeeId, LocalDate day, Instant checkInTime, AttendanceStatus status, UUID actor, Instant now) {
        return new AttendanceRecord(UUID.randomUUID(), employeeId, day, checkInTime, null, status, now, now, actor, actor, null);
    }

    public void checkOut(Instant time, UUID actor, Instant now) {
        if (checkOutTime != null) {
            throw new DomainException("Already checked out");
        }
        this.checkOutTime = time;
        this.updatedAt = now;
        this.lastModifiedBy = actor;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDay() {
        return day;
    }

    public Instant getCheckInTime() {
        return checkInTime;
    }

    public Instant getCheckOutTime() {
        return checkOutTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public boolean isLate(LocalTime start) {
        return checkInTime.atZone(java.time.ZoneOffset.UTC).toLocalTime().isAfter(start);
    }
}
