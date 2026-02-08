package com.example.hr.leave.application;

import com.example.hr.leave.api.LeaveQueryApi;
import com.example.hr.leave.domain.LeaveRequest;
import com.example.hr.leave.domain.LeaveStatus;
import com.example.hr.leave.domain.LeaveType;
import com.example.hr.leave.infra.LeaveRequestRepository;
import com.example.hr.shared.domain.DomainException;
import com.example.hr.shared.domain.TimeProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveService implements LeaveQueryApi {
    private static final int ANNUAL_LEAVE_DAYS = 20;
    private final LeaveRequestRepository repository;
    private final TimeProvider timeProvider;

    public LeaveService(LeaveRequestRepository repository, TimeProvider timeProvider) {
        this.repository = repository;
        this.timeProvider = timeProvider;
    }

    @CacheEvict(cacheNames = "leaveBalance", key = "#employeeId")
    public LeaveRequest create(UUID employeeId, LeaveType type, LocalDate start, LocalDate end,
                               boolean morningHalfDay, boolean afternoonHalfDay, UUID actor) {
        if (end.isBefore(start)) {
            throw new DomainException("End date must be after start date");
        }
        LeaveRequest request = LeaveRequest.create(employeeId, type, start, end, morningHalfDay, afternoonHalfDay, actor, timeProvider.now());
        return repository.save(request);
    }

    @CacheEvict(cacheNames = "leaveBalance", key = "#actor")
    public LeaveRequest approve(UUID requestId, UUID actor) {
        LeaveRequest request = repository.findById(requestId)
                .orElseThrow(() -> new DomainException("Leave request not found"));
        request.approve(actor, timeProvider.now());
        return repository.save(request);
    }

    @CacheEvict(cacheNames = "leaveBalance", key = "#actor")
    public LeaveRequest reject(UUID requestId, UUID actor) {
        LeaveRequest request = repository.findById(requestId)
                .orElseThrow(() -> new DomainException("Leave request not found"));
        request.reject(actor, timeProvider.now());
        return repository.save(request);
    }

    public Page<LeaveRequest> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(cacheNames = "leaveBalance", key = "#employeeId")
    public int leaveBalance(UUID employeeId) {
        LocalDate today = timeProvider.now().atZone(java.time.ZoneOffset.UTC).toLocalDate();
        List<LeaveRequest> approved = repository.findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveStatus.APPROVED, today, today.minusYears(1));
        long used = approved.stream()
                .filter(request -> request.getType() == LeaveType.ANNUAL)
                .mapToLong(request -> ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1)
                .sum();
        return Math.max(0, ANNUAL_LEAVE_DAYS - (int) used);
    }

    @Override
    public List<LeaveRequest> findApprovedLeaves(UUID employeeId, LocalDate day) {
        return repository.findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveStatus.APPROVED, day, day);
    }

    @Override
    public List<LeaveRequest> findApprovedLeaves(UUID employeeId, LocalDate from, LocalDate to) {
        return repository.findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveStatus.APPROVED, to, from);
    }
}
