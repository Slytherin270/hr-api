package com.example.hr.notification.infra;

import com.example.hr.notification.domain.NotificationMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface NotificationRepository extends CrudRepository<NotificationMessage, UUID>, PagingAndSortingRepository<NotificationMessage, UUID> {
}
