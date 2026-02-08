package com.example.hr.identity.infra;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class RbacPolicy {
    public boolean isSelf(UUID employeeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return employeeId.toString().equals(authentication.getName());
    }
}
