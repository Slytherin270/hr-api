package com.example.hr.payroll.application;

import com.example.hr.attendance.api.AttendanceQueryApi;
import com.example.hr.identity.api.EmployeeDirectoryApi;
import com.example.hr.identity.domain.Employee;
import com.example.hr.leave.api.LeaveQueryApi;
import com.example.hr.leave.domain.LeaveRequest;
import com.example.hr.leave.domain.LeaveType;
import com.example.hr.payroll.api.PayrollPaymentApi;
import com.example.hr.payroll.domain.PayrollGenerated;
import com.example.hr.payroll.domain.PayrollRun;
import com.example.hr.payroll.domain.Payslip;
import com.example.hr.payroll.infra.PayrollRunRepository;
import com.example.hr.payroll.infra.PayslipRepository;
import com.example.hr.shared.domain.DomainException;
import com.example.hr.shared.domain.TimeProvider;
import com.example.hr.shared.infra.RedisLockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class PayrollService implements PayrollPaymentApi {
    private static final BigDecimal BASE_SALARY = new BigDecimal("3000.00");
    private static final BigDecimal DAILY_RATE = new BigDecimal("150.00");
    private final PayrollRunRepository runRepository;
    private final PayslipRepository payslipRepository;
    private final EmployeeDirectoryApi employeeDirectoryApi;
    private final AttendanceQueryApi attendanceQueryApi;
    private final LeaveQueryApi leaveQueryApi;
    private final RedisLockService lockService;
    private final ApplicationEventPublisher eventPublisher;
    private final TimeProvider timeProvider;
    private final Duration lockTtl;

    public PayrollService(PayrollRunRepository runRepository,
                          PayslipRepository payslipRepository,
                          EmployeeDirectoryApi employeeDirectoryApi,
                          AttendanceQueryApi attendanceQueryApi,
                          LeaveQueryApi leaveQueryApi,
                          RedisLockService lockService,
                          ApplicationEventPublisher eventPublisher,
                          TimeProvider timeProvider,
                          @Value("${app.payroll.run-lock-seconds}") long lockSeconds) {
        this.runRepository = runRepository;
        this.payslipRepository = payslipRepository;
        this.employeeDirectoryApi = employeeDirectoryApi;
        this.attendanceQueryApi = attendanceQueryApi;
        this.leaveQueryApi = leaveQueryApi;
        this.lockService = lockService;
        this.eventPublisher = eventPublisher;
        this.timeProvider = timeProvider;
        this.lockTtl = Duration.ofSeconds(lockSeconds);
    }

    public PayrollRun runPayroll(YearMonth period, UUID actor) {
        String lockKey = "payroll:run:" + period;
        if (!lockService.acquireLock(lockKey, lockTtl)) {
            throw new DomainException("Payroll run already in progress");
        }
        try {
            runRepository.findByPeriod(period).ifPresent(existing -> {
                throw new DomainException("Payroll already generated for period");
            });
            PayrollRun run = runRepository.save(PayrollRun.create(period, actor, timeProvider.now()));
            List<Employee> employees = employeeDirectoryApi.activeEmployees();
            for (Employee employee : employees) {
                BigDecimal overtime = calculateOvertime(employee.getId(), period);
                BigDecimal unpaidLeave = calculateUnpaidLeave(employee.getId(), period);
                Payslip payslip = Payslip.create(run.getId(), employee.getId(), period, BASE_SALARY, overtime, unpaidLeave, BigDecimal.ZERO, actor, timeProvider.now());
                payslipRepository.save(payslip);
            }
            eventPublisher.publishEvent(new PayrollGenerated(run.getId(), period));
            return run;
        } finally {
            lockService.releaseLock(lockKey);
        }
    }

    public PayrollRun getRun(UUID id) {
        return runRepository.findById(id).orElseThrow(() -> new DomainException("Payroll run not found"));
    }

    @Override
    public PayrollRun markPaid(UUID payrollRunId, UUID actor) {
        PayrollRun run = getRun(payrollRunId);
        run.markPaid(actor, timeProvider.now());
        return runRepository.save(run);
    }

    public List<Payslip> getPayslips(UUID employeeId, YearMonth period) {
        return payslipRepository.findByEmployeeIdAndPeriod(employeeId, period);
    }

    private BigDecimal calculateOvertime(UUID employeeId, YearMonth period) {
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();
        return attendanceQueryApi.list(employeeId, start, end).isEmpty() ? BigDecimal.ZERO : new BigDecimal("0.00");
    }

    private BigDecimal calculateUnpaidLeave(UUID employeeId, YearMonth period) {
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();
        List<LeaveRequest> leaves = leaveQueryApi.findApprovedLeaves(employeeId, start, end);
        long unpaidDays = leaves.stream().filter(request -> request.getType() == LeaveType.UNPAID).count();
        return DAILY_RATE.multiply(BigDecimal.valueOf(unpaidDays));
    }
}
