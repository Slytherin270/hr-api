package com.example.hr.identity.application;

import com.example.hr.identity.domain.Department;
import com.example.hr.identity.infra.DepartmentRepository;
import com.example.hr.shared.domain.TimeProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DepartmentService {
    private final DepartmentRepository repository;
    private final TimeProvider timeProvider;

    public DepartmentService(DepartmentRepository repository, TimeProvider timeProvider) {
        this.repository = repository;
        this.timeProvider = timeProvider;
    }

    @CacheEvict(cacheNames = "departments", allEntries = true)
    public Department create(String name, UUID actor) {
        return repository.save(Department.create(name, actor, timeProvider.now()));
    }

    @Cacheable(cacheNames = "departments")
    public Page<Department> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
