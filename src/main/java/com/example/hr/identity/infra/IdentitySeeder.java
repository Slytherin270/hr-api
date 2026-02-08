package com.example.hr.identity.infra;

import com.example.hr.identity.domain.Employee;
import com.example.hr.identity.domain.Role;
import com.example.hr.shared.domain.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class IdentitySeeder {
    private static final Logger log = LoggerFactory.getLogger(IdentitySeeder.class);

    @Bean
    CommandLineRunner seedAdmin(EmployeeRepository repository, PasswordEncoder encoder, TimeProvider timeProvider) {
        return args -> {
            if (repository.findByEmailAndDeletedFalse("admin@hr.local").isEmpty()) {
                UUID adminId = UUID.randomUUID();
                Employee admin = Employee.create("admin@hr.local", "HR Admin", encoder.encode("admin123"), Role.ADMIN, null, adminId, timeProvider.now());
                repository.save(admin);
                log.info("seeded default admin user email=admin@hr.local password=admin123");
            }
        };
    }
}
