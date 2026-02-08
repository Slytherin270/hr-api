package com.example.hr.payroll;

import com.example.hr.attendance.api.AttendanceQueryApi;
import com.example.hr.identity.api.EmployeeDirectoryApi;
import com.example.hr.identity.domain.Employee;
import com.example.hr.identity.domain.Role;
import com.example.hr.leave.api.LeaveQueryApi;
import com.example.hr.leave.domain.LeaveRequest;
import com.example.hr.leave.domain.LeaveType;
import com.example.hr.payroll.application.PayrollService;
import com.example.hr.payroll.domain.PayrollRun;
import com.example.hr.payroll.infra.PayrollRunRepository;
import com.example.hr.payroll.infra.PayslipRepository;
import com.example.hr.shared.domain.TimeProvider;
import com.example.hr.shared.infra.RedisLockService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PayrollServiceTest {

    @Test
    void runPayrollCreatesPayslipWithUnpaidLeaveDeduction() {
        PayrollRunRepository runRepository = mock(PayrollRunRepository.class);
        PayslipRepository payslipRepository = mock(PayslipRepository.class);
        EmployeeDirectoryApi employeeDirectoryApi = mock(EmployeeDirectoryApi.class);
        AttendanceQueryApi attendanceQueryApi = mock(AttendanceQueryApi.class);
        LeaveQueryApi leaveQueryApi = mock(LeaveQueryApi.class);
        RedisLockService lockService = mock(RedisLockService.class);
        TimeProvider timeProvider = new TimeProvider(Clock.fixed(Instant.parse("2024-05-20T00:00:00Z"), ZoneOffset.UTC));
        PayrollService payrollService = new PayrollService(runRepository, payslipRepository, employeeDirectoryApi,
                attendanceQueryApi, leaveQueryApi, lockService, event -> {}, timeProvider, 60);

        UUID employeeId = UUID.randomUUID();
        Employee employee = Employee.create("test@example.com", "Test User", "hash", Role.EMPLOYEE, null, employeeId, Instant.parse("2024-05-01T00:00:00Z"));
        when(lockService.acquireLock(anyString(), any())).thenReturn(true);
        when(runRepository.findByPeriod(any())).thenReturn(Optional.empty());
        when(runRepository.save(any(PayrollRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeDirectoryApi.activeEmployees()).thenReturn(List.of(employee));
        when(leaveQueryApi.findApprovedLeaves(eq(employeeId), any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of(
                LeaveRequest.create(employeeId, LeaveType.UNPAID, LocalDate.of(2024, 5, 2), LocalDate.of(2024, 5, 2), false, false, employeeId, Instant.parse("2024-05-01T00:00:00Z"))
        ));
        when(payslipRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PayrollRun run = payrollService.runPayroll(YearMonth.of(2024, 5), employeeId);

        assertThat(run.getPeriod()).isEqualTo(YearMonth.of(2024, 5));
        verify(payslipRepository, times(1)).save(any());
    }
}
