package com.example.hr.attendance;

import com.example.hr.attendance.application.AttendanceService;
import com.example.hr.attendance.domain.AttendanceRecord;
import com.example.hr.attendance.domain.AttendanceStatus;
import com.example.hr.attendance.infra.AttendanceRepository;
import com.example.hr.leave.api.LeaveQueryApi;
import com.example.hr.leave.domain.LeaveRequest;
import com.example.hr.leave.domain.LeaveType;
import com.example.hr.shared.domain.DomainException;
import com.example.hr.shared.domain.TimeProvider;
import com.example.hr.shared.infra.RedisLockService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {

    @Test
    void checkInMarksOnTimeWithMorningLeave() {
        AttendanceRepository repository = mock(AttendanceRepository.class);
        LeaveQueryApi leaveQueryApi = mock(LeaveQueryApi.class);
        RedisLockService lockService = mock(RedisLockService.class);
        TimeProvider timeProvider = new TimeProvider(Clock.fixed(Instant.parse("2024-05-20T13:00:00Z"), ZoneOffset.UTC));
        AttendanceService service = new AttendanceService(repository, leaveQueryApi, lockService, timeProvider, 60);

        UUID employeeId = UUID.randomUUID();
        when(lockService.acquireLock(anyString(), any())).thenReturn(true);
        when(repository.findByEmployeeIdAndDay(employeeId, LocalDate.of(2024, 5, 20))).thenReturn(Optional.empty());
        when(leaveQueryApi.findApprovedLeaves(employeeId, LocalDate.of(2024, 5, 20))).thenReturn(List.of(
                LeaveRequest.create(employeeId, LeaveType.ANNUAL, LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20), true, false, employeeId, Instant.parse("2024-05-01T00:00:00Z"))
        ));
        when(repository.save(any(AttendanceRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceRecord record = service.checkIn(employeeId);

        assertThat(record.getStatus()).isEqualTo(AttendanceStatus.ON_TIME_MORNING_LEAVE);
    }

    @Test
    void checkInRejectedOnFullDayLeave() {
        AttendanceRepository repository = mock(AttendanceRepository.class);
        LeaveQueryApi leaveQueryApi = mock(LeaveQueryApi.class);
        RedisLockService lockService = mock(RedisLockService.class);
        TimeProvider timeProvider = new TimeProvider(Clock.fixed(Instant.parse("2024-05-20T09:30:00Z"), ZoneOffset.UTC));
        AttendanceService service = new AttendanceService(repository, leaveQueryApi, lockService, timeProvider, 60);

        UUID employeeId = UUID.randomUUID();
        when(lockService.acquireLock(anyString(), any())).thenReturn(true);
        when(repository.findByEmployeeIdAndDay(employeeId, LocalDate.of(2024, 5, 20))).thenReturn(Optional.empty());
        when(leaveQueryApi.findApprovedLeaves(employeeId, LocalDate.of(2024, 5, 20))).thenReturn(List.of(
                LeaveRequest.create(employeeId, LeaveType.ANNUAL, LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20), false, false, employeeId, Instant.parse("2024-05-01T00:00:00Z"))
        ));

        assertThatThrownBy(() -> service.checkIn(employeeId))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("full-day leave");
    }
}
