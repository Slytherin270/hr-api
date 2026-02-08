package com.example.hr.attendance.infra;

import com.example.hr.attendance.domain.AttendanceRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends CrudRepository<AttendanceRecord, UUID>, PagingAndSortingRepository<AttendanceRecord, UUID> {
    Optional<AttendanceRecord> findByEmployeeIdAndDay(UUID employeeId, LocalDate day);

    List<AttendanceRecord> findByEmployeeIdAndDayBetween(UUID employeeId, LocalDate from, LocalDate to);
}
