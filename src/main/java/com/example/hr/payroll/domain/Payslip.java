package com.example.hr.payroll.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

@Table("payslips")
public class Payslip {
    @Id
    private UUID id;
    private UUID payrollRunId;
    private UUID employeeId;
    private YearMonth period;
    private BigDecimal baseSalary;
    private BigDecimal overtime;
    private BigDecimal unpaidLeaveDeduction;
    private BigDecimal bonus;
    private BigDecimal netPay;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
    @Version
    private Long version;

    public Payslip(UUID id, UUID payrollRunId, UUID employeeId, YearMonth period, BigDecimal baseSalary,
                   BigDecimal overtime, BigDecimal unpaidLeaveDeduction, BigDecimal bonus, BigDecimal netPay,
                   Instant createdAt, Instant updatedAt, UUID createdBy, UUID lastModifiedBy, Long version) {
        this.id = id;
        this.payrollRunId = payrollRunId;
        this.employeeId = employeeId;
        this.period = period;
        this.baseSalary = baseSalary;
        this.overtime = overtime;
        this.unpaidLeaveDeduction = unpaidLeaveDeduction;
        this.bonus = bonus;
        this.netPay = netPay;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

    public static Payslip create(UUID payrollRunId, UUID employeeId, YearMonth period,
                                 BigDecimal baseSalary, BigDecimal overtime, BigDecimal unpaidLeaveDeduction, BigDecimal bonus,
                                 UUID actor, Instant now) {
        BigDecimal netPay = baseSalary.add(overtime).add(bonus).subtract(unpaidLeaveDeduction);
        return new Payslip(UUID.randomUUID(), payrollRunId, employeeId, period, baseSalary, overtime,
                unpaidLeaveDeduction, bonus, netPay, now, now, actor, actor, null);
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public UUID getPayrollRunId() {
        return payrollRunId;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public BigDecimal getNetPay() {
        return netPay;
    }
}
