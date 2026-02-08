package com.example.hr.payroll.domain;

import java.time.YearMonth;
import java.util.UUID;

public record PayrollGenerated(UUID payrollRunId, YearMonth period) {
}
