package com.example.hr.payroll.infra;

import com.example.hr.payroll.domain.PayrollRun;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

public interface PayrollRunRepository extends CrudRepository<PayrollRun, UUID>, PagingAndSortingRepository<PayrollRun, UUID> {
    Optional<PayrollRun> findByPeriod(YearMonth period);
}
