package com.example.hr.payment.infra;

import com.example.hr.payment.domain.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID>, PagingAndSortingRepository<Payment, UUID> {
}
