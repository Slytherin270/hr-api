package com.example.hr.payroll.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

@Table("payroll_runs")
public class PayrollRun {
    @Id
    private UUID id;
    private YearMonth period;
    private PayrollStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
    @Version
    private Long version;

    public PayrollRun(UUID id, YearMonth period, PayrollStatus status, Instant createdAt, Instant updatedAt,
                      UUID createdBy, UUID lastModifiedBy, Long version) {
        this.id = id;
        this.period = period;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

    public static PayrollRun create(YearMonth period, UUID actor, Instant now) {
        return new PayrollRun(UUID.randomUUID(), period, PayrollStatus.GENERATED, now, now, actor, actor, null);
    }

    public void markPaid(UUID actor, Instant now) {
        this.status = PayrollStatus.PAID;
        this.updatedAt = now;
        this.lastModifiedBy = actor;
    }

    public UUID getId() {
        return id;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public PayrollStatus getStatus() {
        return status;
    }
}
