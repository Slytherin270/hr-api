package com.example.hr.attendance.api;

import com.example.hr.attendance.domain.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceQueryApi {
    List<AttendanceRecord> list(UUID employeeId, LocalDate from, LocalDate to);
}
