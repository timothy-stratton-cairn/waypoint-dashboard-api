package com.cairn.waypoint.dashboard.configuration.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI waypointAuthorizationApiOpenApi() {
        return new OpenAPI()
                .addServersItem(new Server().description("Dev Server").url("http://96.61.158.12:8083"))
                .addServersItem(new Server().description("Local Server").url("http://localhost:8083"))
                .info(new Info()
                        .title("Waypoint - Dashboard API")
                        .description("Waypoint - Dashboard API serves out all application data for Waypoint")
                        .version("LATEST"))
                .components(
                        new Components().addSecuritySchemes("oAuth2JwtBearer",
                                new SecurityScheme().name("oAuth2JwtBearer")
                                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
