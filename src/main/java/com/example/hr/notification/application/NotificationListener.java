package com.example.hr.notification.application;

import com.example.hr.notification.domain.NotificationMessage;
import com.example.hr.notification.infra.NotificationRepository;
import com.example.hr.payment.domain.PayrollPaid;
import com.example.hr.payroll.domain.PayrollGenerated;
import com.example.hr.shared.domain.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    private final NotificationRepository repository;
    private final TimeProvider timeProvider;

    public NotificationListener(NotificationRepository repository, TimeProvider timeProvider) {
        this.repository = repository;
        this.timeProvider = timeProvider;
    }

    @EventListener
    public void handlePayrollGenerated(PayrollGenerated event) {
        String payload = "Payroll generated for period " + event.period();
        log.info("notification=payroll_generated payrollRunId={}", event.payrollRunId());
        repository.save(NotificationMessage.of("EMAIL", payload, event.payrollRunId(), timeProvider.now()));
    }

    @EventListener
    public void handlePayrollPaid(PayrollPaid event) {
        String payload = "Payroll paid for run " + event.payrollRunId();
        log.info("notification=payroll_paid payrollRunId={} paymentId={}", event.payrollRunId(), event.paymentId());
        repository.save(NotificationMessage.of("EMAIL", payload, event.paymentId(), timeProvider.now()));
    }
}
