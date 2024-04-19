package com.cairn.waypoint.dashboard.configuration.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String authServerUri;

  @Bean(name = "authorizationApiWebClient")
  public WebClient authorizationApiWebClient() {
    return WebClient.create(authServerUri);
  }
}
