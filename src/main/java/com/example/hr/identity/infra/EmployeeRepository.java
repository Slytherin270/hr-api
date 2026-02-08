package com.example.hr.identity.infra;

import com.example.hr.identity.domain.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends CrudRepository<Employee, UUID>, PagingAndSortingRepository<Employee, UUID> {
    Optional<Employee> findByEmailAndDeletedFalse(String email);

    Page<Employee> findByDeletedFalse(Pageable pageable);
}
