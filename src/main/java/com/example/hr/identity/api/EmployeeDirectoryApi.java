package com.example.hr.identity.api;

import com.example.hr.identity.domain.Employee;

import java.util.List;

public interface EmployeeDirectoryApi {
    List<Employee> activeEmployees();
}
