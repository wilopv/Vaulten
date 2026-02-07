package com.wilove.vaulten.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 */
@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI vaultenOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Vaulten API")
                                                .description("Backend REST API for password manager - Portfolio project")
                                                .version("v0.1.0 - Phase 0")
                                                .contact(new Contact()
                                                                .name("Vaulten Development")
                                                                .url("https://github.com/wilopv/vaulten"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                // JWT Security will be configured in Phase 1
                                .components(new Components()
                                                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("JWT authentication (will be implemented in Phase 1)")));
        }
}
