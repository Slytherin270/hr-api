package com.example.hr.identity;

import com.example.hr.identity.domain.Employee;
import com.example.hr.identity.domain.Role;
import com.example.hr.identity.infra.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@TestPropertySource(properties = {\"spring.flyway.enabled=true\", \"spring.flyway.locations=classpath:db/migration\"})
@Testcontainers
class EmployeeRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EmployeeRepository repository;

    @Test
    void savesEmployee() {
        Instant now = Instant.parse("2024-05-01T00:00:00Z");
        Employee employee = new Employee(UUID.randomUUID(), "user@example.com", "User", "hash", Role.EMPLOYEE, null, true, false, now, now, UUID.randomUUID(), UUID.randomUUID(), null);
        repository.save(employee);

        assertThat(repository.findByEmailAndDeletedFalse("user@example.com")).isPresent();
    }
}
