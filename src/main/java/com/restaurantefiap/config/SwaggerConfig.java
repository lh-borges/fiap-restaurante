package com.restaurantefiap.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Postech - ADJ - fase 1 - Tech Challenge | API Restaurante")
                        .version("1.0")
                        .description("Documentação da API para o Postech - ADJ - fase 1 - Tech Challenge")
                        .contact(new Contact()
                                .name("Equipe 8")
                                .email("danilo.bossanova@hotmail.com")
                                .url("https://www.fiap.com.br")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth", List.of()))
                .components(new Components() 
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