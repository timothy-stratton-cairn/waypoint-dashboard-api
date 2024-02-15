package com.cairn.waypoint.dashboard.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
public class JWTSecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests((authorize) -> authorize
//                    .requestMatchers("/foos/**").hasAuthority("SCOPE_protocol.read")
//                    .requestMatchers("/foos").hasAuthority("SCOPE_protocol.read")
//                    .anyRequest().authenticated()
//            )
//            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
//        return http.build();
//    }
}
