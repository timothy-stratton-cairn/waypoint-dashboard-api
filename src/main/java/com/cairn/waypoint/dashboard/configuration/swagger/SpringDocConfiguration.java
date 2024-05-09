package com.cairn.waypoint.dashboard.configuration.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class SpringDocConfiguration {

  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  public SpringDocConfiguration(MappingJackson2HttpMessageConverter converter) {
    var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
    supportedMediaTypes.add(new MediaType("application", "octet-stream"));
    converter.setSupportedMediaTypes(supportedMediaTypes);
  }

  @Bean
  public OpenAPI waypointAuthorizationApiOpenApi() {
    return new OpenAPI()
        .addServersItem(new Server().description("This server").url(baseUrl))
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
