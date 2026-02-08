package com.example.hr.leave.api;

import com.example.hr.leave.application.LeaveService;
import com.example.hr.leave.domain.LeaveRequest;
import com.example.hr.leave.domain.LeaveType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leave")
public class LeaveController {
    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/requests")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<LeaveResponse> create(@Valid @RequestBody CreateLeaveRequest request) {
        UUID actor = currentEmployee();
        LeaveRequest leave = leaveService.create(actor, request.type(), request.startDate(), request.endDate(),
                request.morningHalfDay(), request.afternoonHalfDay(), actor);
        return ResponseEntity.ok(LeaveResponse.from(leave));
    }

    @PostMapping("/requests/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<LeaveResponse> approve(@PathVariable UUID id) {
        UUID actor = currentEmployee();
        return ResponseEntity.ok(LeaveResponse.from(leaveService.approve(id, actor)));
    }

    @PostMapping("/requests/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<LeaveResponse> reject(@PathVariable UUID id) {
        UUID actor = currentEmployee();
        return ResponseEntity.ok(LeaveResponse.from(leaveService.reject(id, actor)));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Page<LeaveResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(leaveService.list(pageable).map(LeaveResponse::from));
    }

    @GetMapping("/balance/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or @rbacPolicy.isSelf(#employeeId)")
    public ResponseEntity<LeaveBalanceResponse> balance(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(new LeaveBalanceResponse(employeeId, leaveService.leaveBalance(employeeId)));
    }

    private UUID currentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    public record CreateLeaveRequest(@NotNull LeaveType type,
                                     @NotNull LocalDate startDate,
                                     @NotNull LocalDate endDate,
                                     boolean morningHalfDay,
                                     boolean afternoonHalfDay) {
    }

    public record LeaveResponse(UUID id, UUID employeeId, LeaveType type, String status, LocalDate startDate,
                                LocalDate endDate, boolean morningHalfDay, boolean afternoonHalfDay) {
        public static LeaveResponse from(LeaveRequest request) {
            return new LeaveResponse(request.getId(), request.getEmployeeId(), request.getType(), request.getStatus().name(),
                    request.getStartDate(), request.getEndDate(), request.isMorningHalfDay(), request.isAfternoonHalfDay());
        }
    }

    public record LeaveBalanceResponse(UUID employeeId, int remainingDays) {
    }
}
