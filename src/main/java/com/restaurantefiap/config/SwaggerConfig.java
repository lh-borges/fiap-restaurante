package com.restaurantefiap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // informações da API
                .info(new Info()
                        .title("API Restaurante")
                        .version("1.0")
                        .description("Documentação da API para o projeto Fase 1")
                        .contact(new Contact()
                                .name("Equipe A")
                                .email("restaurante@fiap.com.br")
                                .url("https://www.fiap.com.br")
                        )
                )
                // configuração de segurança JWT
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth", java.util.Collections.emptyList()))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
