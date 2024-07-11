package com.cairn.waypoint.dashboard.configuration.security;

import com.cairn.waypoint.dashboard.endpoints.filedownload.DownloadDBBackupFileEndpoint;
import com.cairn.waypoint.dashboard.endpoints.filedownload.DownloadHomeworkResponseEndpoint;
import com.cairn.waypoint.dashboard.endpoints.filedownload.DownloadStepAttachmentEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers(DownloadHomeworkResponseEndpoint.PATH).permitAll()
            .requestMatchers(DownloadDBBackupFileEndpoint.PATH).permitAll()
            .requestMatchers(DownloadStepAttachmentEndpoint.PATH).permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/health").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
  }
}
