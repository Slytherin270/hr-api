package com.example.hr.shared.infra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "HR API", version = "v1", description = "HR Management System API")
)
public class OpenApiConfig {
}
