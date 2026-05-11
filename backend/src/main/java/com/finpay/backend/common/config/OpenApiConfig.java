package com.finpay.backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName =
                "bearerAuth";

        return new OpenAPI()

                .info(
                        new Info()
                                .title(
                                        "FinPay API"
                                )
                                .version("1.0")
                                .description(
                                        """
                                        Secure fintech backend platform
                                        built using Spring Boot,
                                        PostgreSQL, Redis, JWT,
                                        and RBAC.
                                        """
                                )
                                .contact(
                                        new Contact()
                                                .name(
                                                        "Aditya Sinha"
                                                )
                                )
                                .license(
                                        new License()
                                                .name(
                                                        "MIT License"
                                                )
                                )
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(
                                        securitySchemeName
                                )
                )

                .schemaRequirement(
                        securitySchemeName,

                        new SecurityScheme()
                                .name(
                                        securitySchemeName
                                )
                                .type(
                                        SecurityScheme.Type.HTTP
                                )
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );
    }
}