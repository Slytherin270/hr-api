package com.example.hr.payroll.infra;

import com.example.hr.payroll.domain.Payslip;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface PayslipRepository extends CrudRepository<Payslip, UUID>, PagingAndSortingRepository<Payslip, UUID> {
    List<Payslip> findByEmployeeIdAndPeriod(UUID employeeId, YearMonth period);
}
