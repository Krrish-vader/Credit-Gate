package com.creditgate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to set up OpenAPI/Swagger documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CreditGate Loan/Credit Approval Engine API")
                        .version("1.0.0")
                        .description("REST API for CreditGate. Submit loan applications, execute pluggable eligibility rules, assign risk-based rate tiers, and retrieve detailed audit records for full explainability."));
    }
}
