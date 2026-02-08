package com.example.hr.leave;

import com.example.hr.leave.application.LeaveService;
import com.example.hr.leave.domain.LeaveRequest;
import com.example.hr.leave.domain.LeaveStatus;
import com.example.hr.leave.domain.LeaveType;
import com.example.hr.leave.infra.LeaveRequestRepository;
import com.example.hr.shared.domain.TimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeaveServiceTest {

    @Test
    void leaveBalanceDeductsAnnualLeaveDays() {
        LeaveRequestRepository repository = mock(LeaveRequestRepository.class);
        TimeProvider timeProvider = new TimeProvider(Clock.fixed(Instant.parse("2024-05-20T00:00:00Z"), ZoneOffset.UTC));
        LeaveService service = new LeaveService(repository, timeProvider);

        UUID employeeId = UUID.randomUUID();
        LeaveRequest approved = new LeaveRequest(UUID.randomUUID(), employeeId, LeaveType.ANNUAL, LeaveStatus.APPROVED,
                LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 3), false, false,
                Instant.parse("2024-05-01T00:00:00Z"), Instant.parse("2024-05-01T00:00:00Z"), employeeId, employeeId, null);

        when(repository.findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(employeeId, LeaveStatus.APPROVED,
                LocalDate.of(2024, 5, 20), LocalDate.of(2023, 5, 20))).thenReturn(List.of(approved));

        assertThat(service.leaveBalance(employeeId)).isEqualTo(17);
    }
}
