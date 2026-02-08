package com.example.hr.shared.infra.config;

import com.example.hr.shared.domain.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeProviderConfig {
    @Bean
    public TimeProvider timeProvider() {
        return TimeProvider.systemUtc();
    }
}
