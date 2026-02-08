package com.example.hr.attendance.application;

import com.example.hr.attendance.api.AttendanceQueryApi;
import com.example.hr.attendance.domain.AttendanceRecord;
import com.example.hr.attendance.domain.AttendanceStatus;
import com.example.hr.attendance.infra.AttendanceRepository;
import com.example.hr.leave.api.LeaveQueryApi;
import com.example.hr.leave.domain.LeaveRequest;
import com.example.hr.shared.domain.DomainException;
import com.example.hr.shared.domain.TimeProvider;
import com.example.hr.shared.infra.RedisLockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService implements AttendanceQueryApi {
    private final AttendanceRepository repository;
    private final LeaveQueryApi leaveQueryApi;
    private final RedisLockService lockService;
    private final TimeProvider timeProvider;
    private final Duration checkInLockTtl;

    public AttendanceService(AttendanceRepository repository,
                             LeaveQueryApi leaveQueryApi,
                             RedisLockService lockService,
                             TimeProvider timeProvider,
                             @Value("${app.attendance.check-in-lock-seconds}") long lockSeconds) {
        this.repository = repository;
        this.leaveQueryApi = leaveQueryApi;
        this.lockService = lockService;
        this.timeProvider = timeProvider;
        this.checkInLockTtl = Duration.ofSeconds(lockSeconds);
    }

    public AttendanceRecord checkIn(UUID employeeId) {
        String lockKey = "attendance:checkin:" + employeeId;
        if (!lockService.acquireLock(lockKey, checkInLockTtl)) {
            throw new DomainException("Duplicate check-in detected");
        }
        try {
            Instant now = timeProvider.now();
            LocalDate day = now.atZone(ZoneOffset.UTC).toLocalDate();
            repository.findByEmployeeIdAndDay(employeeId, day).ifPresent(existing -> {
                throw new DomainException("Already checked in for today");
            });
            AttendanceStatus status = resolveStatus(employeeId, day, now);
            AttendanceRecord record = AttendanceRecord.checkIn(employeeId, day, now, status, employeeId, now);
            return repository.save(record);
        } finally {
            lockService.releaseLock(lockKey);
        }
    }

    public AttendanceRecord checkOut(UUID employeeId) {
        String lockKey = "attendance:checkout:" + employeeId;
        if (!lockService.acquireLock(lockKey, checkInLockTtl)) {
            throw new DomainException("Duplicate check-out detected");
        }
        try {
            Instant now = timeProvider.now();
            LocalDate day = now.atZone(ZoneOffset.UTC).toLocalDate();
            AttendanceRecord record = repository.findByEmployeeIdAndDay(employeeId, day)
                    .orElseThrow(() -> new DomainException("No check-in found"));
            record.checkOut(now, employeeId, now);
            return repository.save(record);
        } finally {
            lockService.releaseLock(lockKey);
        }
    }

    @Override
    public List<AttendanceRecord> list(UUID employeeId, LocalDate from, LocalDate to) {
        return repository.findByEmployeeIdAndDayBetween(employeeId, from, to);
    }

    private AttendanceStatus resolveStatus(UUID employeeId, LocalDate day, Instant checkIn) {
        List<LeaveRequest> leaves = leaveQueryApi.findApprovedLeaves(employeeId, day);
        boolean fullDayLeave = leaves.stream().anyMatch(request -> !request.isMorningHalfDay() && !request.isAfternoonHalfDay());
        if (fullDayLeave) {
            throw new DomainException("Cannot check-in while on approved full-day leave");
        }
        boolean morningLeave = leaves.stream().anyMatch(LeaveRequest::isMorningHalfDay);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime checkInTime = checkIn.atZone(ZoneOffset.UTC).toLocalTime();
        if (morningLeave && checkInTime.isAfter(LocalTime.NOON.minusMinutes(1))) {
            return AttendanceStatus.ON_TIME_MORNING_LEAVE;
        }
        return checkInTime.isAfter(start) ? AttendanceStatus.LATE : AttendanceStatus.ON_TIME;
    }
}
