package com.example.hr.leave.api;

import com.example.hr.leave.domain.LeaveRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LeaveQueryApi {
    List<LeaveRequest> findApprovedLeaves(UUID employeeId, LocalDate day);

    List<LeaveRequest> findApprovedLeaves(UUID employeeId, LocalDate from, LocalDate to);
}
