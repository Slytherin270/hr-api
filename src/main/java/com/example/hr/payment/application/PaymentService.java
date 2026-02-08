package com.example.hr.payment.application;

import com.example.hr.payment.domain.Payment;
import com.example.hr.payment.domain.PayrollPaid;
import com.example.hr.payment.infra.PaymentRepository;
import com.example.hr.payroll.api.PayrollPaymentApi;
import com.example.hr.shared.domain.DomainException;
import com.example.hr.shared.domain.TimeProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PayrollPaymentApi payrollPaymentApi;
    private final ApplicationEventPublisher eventPublisher;
    private final TimeProvider timeProvider;

    public PaymentService(PaymentRepository paymentRepository,
                          PayrollPaymentApi payrollPaymentApi,
                          ApplicationEventPublisher eventPublisher,
                          TimeProvider timeProvider) {
        this.paymentRepository = paymentRepository;
        this.payrollPaymentApi = payrollPaymentApi;
        this.eventPublisher = eventPublisher;
        this.timeProvider = timeProvider;
    }

    public Payment pay(UUID payrollRunId, UUID actor) {
        Payment payment = paymentRepository.save(Payment.initiate(payrollRunId, actor, timeProvider.now()));
        payrollPaymentApi.markPaid(payrollRunId, actor);
        payment.complete(actor, timeProvider.now());
        Payment completed = paymentRepository.save(payment);
        eventPublisher.publishEvent(new PayrollPaid(payrollRunId, completed.getId()));
        return completed;
    }

    public Payment get(UUID id) {
        return paymentRepository.findById(id).orElseThrow(() -> new DomainException("Payment not found"));
    }
}
