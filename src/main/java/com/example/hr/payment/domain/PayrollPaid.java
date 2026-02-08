package com.example.hr.payment.domain;

import java.util.UUID;

public record PayrollPaid(UUID payrollRunId, UUID paymentId) {
}
