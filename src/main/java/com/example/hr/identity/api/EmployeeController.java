package com.example.hr.identity.api;

import com.example.hr.identity.application.EmployeeService;
import com.example.hr.identity.domain.Employee;
import com.example.hr.identity.domain.Role;
import com.example.hr.identity.infra.RbacPolicy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final RbacPolicy rbacPolicy;

    public EmployeeController(EmployeeService employeeService, RbacPolicy rbacPolicy) {
        this.employeeService = employeeService;
        this.rbacPolicy = rbacPolicy;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        UUID actor = currentEmployee();
        Employee employee = employeeService.create(request.email(), request.fullName(), request.password(), request.role(), request.departmentId(), actor);
        return ResponseEntity.ok(EmployeeResponse.from(employee));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @rbacPolicy.isSelf(#id)")
    public ResponseEntity<EmployeeResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(EmployeeResponse.from(employeeService.get(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EmployeeResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(employeeService.list(pageable).map(EmployeeResponse::from));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @rbacPolicy.isSelf(#id)")
    public ResponseEntity<EmployeeResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest request) {
        UUID actor = currentEmployee();
        Employee employee = employeeService.update(id, request.fullName(), request.departmentId(), actor);
        return ResponseEntity.ok(EmployeeResponse.from(employee));
    }

    private UUID currentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    public record CreateEmployeeRequest(@Email @NotBlank String email,
                                        @NotBlank String fullName,
                                        @NotBlank String password,
                                        @NotNull Role role,
                                        UUID departmentId) {
    }

    public record UpdateEmployeeRequest(@NotBlank String fullName, UUID departmentId) {
    }

    public record EmployeeResponse(UUID id, String email, String fullName, Role role, UUID departmentId) {
        public static EmployeeResponse from(Employee employee) {
            return new EmployeeResponse(employee.getId(), employee.getEmail(), employee.getFullName(), employee.getRole(), employee.getDepartmentId());
        }
    }
}
