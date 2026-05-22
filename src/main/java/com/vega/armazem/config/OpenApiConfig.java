package com.vega.armazem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI armazemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Armazém de Ingredientes API")
                        .description("API RESTful para gerenciamento de estoque, compartimentos e movimentações de ingredientes.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Victor Campolina")
                                .email("victor.cs2001.vc@gmail.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório do projeto")
                        .url("https://github.com/Victor110901/armazen-ingedientes-backend"));
    }
}