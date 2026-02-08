package com.example.hr.attendance.api;

import com.example.hr.attendance.application.AttendanceService;
import com.example.hr.attendance.domain.AttendanceRecord;
import com.example.hr.attendance.domain.AttendanceStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<AttendanceResponse> checkIn() {
        UUID employeeId = currentEmployee();
        AttendanceRecord record = attendanceService.checkIn(employeeId);
        return ResponseEntity.ok(AttendanceResponse.from(record));
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<AttendanceResponse> checkOut() {
        UUID employeeId = currentEmployee();
        AttendanceRecord record = attendanceService.checkOut(employeeId);
        return ResponseEntity.ok(AttendanceResponse.from(record));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<List<AttendanceResponse>> myAttendance(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        UUID employeeId = currentEmployee();
        return ResponseEntity.ok(attendanceService.list(employeeId, from, to).stream().map(AttendanceResponse::from).toList());
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<AttendanceResponse>> employeeAttendance(@PathVariable UUID id, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return ResponseEntity.ok(attendanceService.list(id, from, to).stream().map(AttendanceResponse::from).toList());
    }

    private UUID currentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    public record AttendanceResponse(UUID id, UUID employeeId, LocalDate day, AttendanceStatus status) {
        public static AttendanceResponse from(AttendanceRecord record) {
            return new AttendanceResponse(record.getId(), record.getEmployeeId(), record.getDay(), record.getStatus());
        }
    }
}
