package com.example.hr.identity.api;

import com.example.hr.identity.application.DepartmentService;
import com.example.hr.identity.domain.Department;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody CreateDepartmentRequest request) {
        UUID actor = currentEmployee();
        Department department = departmentService.create(request.name(), actor);
        return ResponseEntity.ok(DepartmentResponse.from(department));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<Page<DepartmentResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(departmentService.list(pageable).map(DepartmentResponse::from));
    }

    private UUID currentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    public record CreateDepartmentRequest(@NotBlank String name) {
    }

    public record DepartmentResponse(UUID id, String name) {
        public static DepartmentResponse from(Department department) {
            return new DepartmentResponse(department.getId(), department.getName());
        }
    }
}
