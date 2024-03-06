package com.cairn.waypoint.dashboard.configuration.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Bean(name = "authorizationApiWebClient")
  public WebClient authorizationApiWebClient() {
    return WebClient.create("http://localhost:8082");
  }
}
