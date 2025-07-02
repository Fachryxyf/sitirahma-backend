package com.sitirahma.rekomendasi_buku.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        @Order(1)
        public SecurityFilterChain publicApiFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher(new AntPathRequestMatcher("/api/v1/auth/**"))
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain privateApiFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(withDefaults())
                                .authorizeHttpRequests(req -> req
                                                // Mengizinkan GET untuk semua user terotentikasi
                                                .requestMatchers(HttpMethod.GET, "/api/v1/buku/**")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                                                // Mengizinkan semua metode untuk admin di path admin
                                                .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}