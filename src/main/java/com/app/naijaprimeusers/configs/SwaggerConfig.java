package com.app.naijaprimeusers.configs;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi controllerApi() {
        return GroupedOpenApi.builder()
                .group("naija-prime-apis")
                .displayName("Naija Prime Rest Apis")
                .packagesToScan("com.app.naijaprimeusers.restControllers") // Specify the package to scan
                .build();}

}