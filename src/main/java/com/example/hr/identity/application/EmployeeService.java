package com.example.hr.identity.application;

import com.example.hr.identity.api.EmployeeDirectoryApi;
import com.example.hr.identity.domain.Employee;
import com.example.hr.identity.domain.Role;
import com.example.hr.identity.infra.EmployeeRepository;
import com.example.hr.shared.domain.DomainException;
import com.example.hr.shared.domain.TimeProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmployeeService implements EmployeeDirectoryApi {
    private final EmployeeRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TimeProvider timeProvider;

    public EmployeeService(EmployeeRepository repository, PasswordEncoder passwordEncoder, TimeProvider timeProvider) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.timeProvider = timeProvider;
    }

    public Employee create(String email, String fullName, String rawPassword, Role role, UUID departmentId, UUID actor) {
        repository.findByEmailAndDeletedFalse(email).ifPresent(existing -> {
            throw new DomainException("Employee already exists");
        });
        String hash = passwordEncoder.encode(rawPassword);
        Employee employee = Employee.create(email, fullName, hash, role, departmentId, actor, timeProvider.now());
        evictEmployeeCache(employee.getId());
        return repository.save(employee);
    }

    @CacheEvict(cacheNames = "employeeProfiles", key = "#id")
    public Employee update(UUID id, String fullName, UUID departmentId, UUID actor) {
        Employee employee = repository.findById(id)
                .filter(existing -> !existing.isDeleted())
                .orElseThrow(() -> new DomainException("Employee not found"));
        employee.updateProfile(fullName, departmentId, actor, timeProvider.now());
        return repository.save(employee);
    }

    @Cacheable(cacheNames = "employeeProfiles", key = "#id")
    public Employee get(UUID id) {
        return repository.findById(id)
                .filter(existing -> !existing.isDeleted())
                .orElseThrow(() -> new DomainException("Employee not found"));
    }

    public Page<Employee> list(Pageable pageable) {
        return repository.findByDeletedFalse(pageable).map(emp -> emp);
    }

    @CacheEvict(cacheNames = "employeeProfiles", key = "#id")
    public void evictEmployeeCache(UUID id) {
        // cache eviction hook
    }

    @Override
    public java.util.List<Employee> activeEmployees() {
        return ((java.util.List<Employee>) repository.findAll()).stream()
                .filter(employee -> !employee.isDeleted())
                .toList();
    }
}
