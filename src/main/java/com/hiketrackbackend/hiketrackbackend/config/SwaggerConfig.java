package com.hiketrackbackend.hiketrackbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("HikeTrack API")
                        .version("1.0")
                        .description("API documentation for HikeTrack application")
                        .termsOfService("https://github.com/HikeTrack/HikeTrack-Backend/blob/main/LICENSE")
                        .contact(new Contact()
                                .name("Vadym Pantielieienko")
                                .email("vadympantielieienko@gmail.com")
                                .url("https://www.linkedin.com/in/vadympantielieienko/")));
    }
}
