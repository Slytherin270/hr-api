package com.example.hr.identity.application;

import com.example.hr.identity.domain.Employee;
import com.example.hr.identity.infra.EmployeeRepository;
import com.example.hr.identity.infra.JwtService;
import com.example.hr.shared.domain.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final EmployeeRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(EmployeeRepository repository, PasswordEncoder encoder, JwtService jwtService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String password) {
        Employee employee = repository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new DomainException("Invalid credentials"));
        if (!encoder.matches(password, employee.getPasswordHash())) {
            throw new DomainException("Invalid credentials");
        }
        return jwtService.generateToken(employee.getId(), employee.getEmail(), employee.getRole());
    }
}
