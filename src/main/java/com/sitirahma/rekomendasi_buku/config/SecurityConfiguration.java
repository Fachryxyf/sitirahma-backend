package com.sitirahma.rekomendasi_buku.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // 1. Menerapkan konfigurasi CORS yang kita definisikan di bawah
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // 2. Menonaktifkan CSRF
                                .csrf(AbstractHttpConfigurer::disable)

                                // 3. Mendefinisikan aturan otorisasi
                                .authorizeHttpRequests(req -> req
                                                .requestMatchers("/api/v1/auth/**").permitAll() // Endpoint otentikasi
                                                                                                // publik
                                                .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN") // Endpoint
                                                                                                                // admin
                                                .anyRequest().authenticated() // Semua request lain wajib login
                                )

                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // 4. Mendefinisikan Bean untuk sumber konfigurasi CORS
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Izinkan permintaan dari Vercel dan localhost Anda
                configuration.setAllowedOrigins(
                                List.of("https://sitirahma-frontend.vercel.app", "http://localhost:5173"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/api/**", configuration); // Terapkan konfigurasi ini untuk semua path
                                                                            // API kita
                return source;
        }
}