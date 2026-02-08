package com.example.hr.leave.infra;

import com.example.hr.leave.domain.LeaveRequest;
import com.example.hr.leave.domain.LeaveStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LeaveRequestRepository extends CrudRepository<LeaveRequest, UUID>, PagingAndSortingRepository<LeaveRequest, UUID> {
    List<LeaveRequest> findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UUID employeeId, LeaveStatus status, LocalDate end, LocalDate start);
}
