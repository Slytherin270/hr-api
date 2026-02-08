package com.example.hr.identity.infra;

import com.example.hr.identity.domain.Department;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface DepartmentRepository extends CrudRepository<Department, UUID>, PagingAndSortingRepository<Department, UUID> {
}
