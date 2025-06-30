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

import static org.springframework.security.config.Customizer.withDefaults;
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
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(withDefaults())
                                .authorizeHttpRequests(auth -> auth
                                                // Izinkan akses publik ke semua endpoint di bawah /auth
                                                .requestMatchers("/api/v1/auth/**").permitAll()

                                                // PERBAIKAN UTAMA: Menggunakan hasAnyAuthority yang lebih eksplisit
                                                // Izinkan akses jika pengguna memiliki peran "ROLE_ADMIN" ATAU
                                                // "ROLE_USER"
                                                .requestMatchers("/api/v1/buku/**")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")

                                                // Untuk semua request lainnya, wajibkan autentikasi
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}