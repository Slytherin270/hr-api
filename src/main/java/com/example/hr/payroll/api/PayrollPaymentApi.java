package com.example.hr.payroll.api;

import com.example.hr.payroll.domain.PayrollRun;

import java.util.UUID;

public interface PayrollPaymentApi {
    PayrollRun markPaid(UUID payrollRunId, UUID actor);
}
