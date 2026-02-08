package com.example.hr.payment.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("payments")
public class Payment {
    @Id
    private UUID id;
    private UUID payrollRunId;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
    @Version
    private Long version;

    public Payment(UUID id, UUID payrollRunId, PaymentStatus status, Instant createdAt, Instant updatedAt,
                   UUID createdBy, UUID lastModifiedBy, Long version) {
        this.id = id;
        this.payrollRunId = payrollRunId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

    public static Payment initiate(UUID payrollRunId, UUID actor, Instant now) {
        return new Payment(UUID.randomUUID(), payrollRunId, PaymentStatus.PENDING, now, now, actor, actor, null);
    }

    public void complete(UUID actor, Instant now) {
        this.status = PaymentStatus.COMPLETED;
        this.updatedAt = now;
        this.lastModifiedBy = actor;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPayrollRunId() {
        return payrollRunId;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
