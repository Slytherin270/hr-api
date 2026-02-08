package com.example.hr.payroll.api;

import com.example.hr.payroll.application.PayrollService;
import com.example.hr.payroll.domain.PayrollRun;
import com.example.hr.payroll.domain.PayrollStatus;
import com.example.hr.payroll.domain.Payslip;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payroll")
public class PayrollController {
    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PayrollRunResponse> run(@RequestParam YearMonth month) {
        UUID actor = currentEmployee();
        PayrollRun run = payrollService.runPayroll(month, actor);
        return ResponseEntity.ok(PayrollRunResponse.from(run));
    }

    @GetMapping("/runs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PayrollRunResponse> getRun(@PathVariable UUID id) {
        return ResponseEntity.ok(PayrollRunResponse.from(payrollService.getRun(id)));
    }

    @GetMapping("/payslips/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or @rbacPolicy.isSelf(#employeeId)")
    public ResponseEntity<List<PayslipResponse>> payslips(@PathVariable UUID employeeId, @RequestParam YearMonth month) {
        List<Payslip> payslips = payrollService.getPayslips(employeeId, month);
        return ResponseEntity.ok(payslips.stream().map(PayslipResponse::from).toList());
    }

    private UUID currentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    public record PayrollRunResponse(UUID id, YearMonth period, PayrollStatus status) {
        public static PayrollRunResponse from(PayrollRun run) {
            return new PayrollRunResponse(run.getId(), run.getPeriod(), run.getStatus());
        }
    }

    public record PayslipResponse(UUID id, UUID payrollRunId, UUID employeeId, YearMonth period) {
        public static PayslipResponse from(Payslip payslip) {
            return new PayslipResponse(payslip.getId(), payslip.getPayrollRunId(), payslip.getEmployeeId(), payslip.getPeriod());
        }
    }
}
