package com.example.hr.payment.api;

import com.example.hr.payment.application.PaymentService;
import com.example.hr.payment.domain.Payment;
import com.example.hr.payment.domain.PaymentStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payroll/{payrollRunId}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> pay(@PathVariable UUID payrollRunId) {
        UUID actor = currentEmployee();
        Payment payment = paymentService.pay(payrollRunId, actor);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(PaymentResponse.from(paymentService.get(id)));
    }

    private UUID currentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    public record PaymentResponse(UUID id, UUID payrollRunId, PaymentStatus status) {
        public static PaymentResponse from(Payment payment) {
            return new PaymentResponse(payment.getId(), payment.getPayrollRunId(), payment.getStatus());
        }
    }
}
