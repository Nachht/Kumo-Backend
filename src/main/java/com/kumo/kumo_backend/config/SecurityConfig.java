package com.kumo.kumo_backend.config;

import com.kumo.kumo_backend.filter.JwtFilter;
import com.kumo.kumo_backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;  // ✅ IMPORTAR ESTO
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                // Endpoints publicos (sin autenticacion)
                                .requestMatchers(
                                        "/auth/**",
                                        "/api/products/public/**",
                                        "/api/categories/public/**",
                                        "/api/public/**",
                                        "/h2-console/**"
                                ).permitAll()

                                // Cliente - Compras y cuenta personal
                                .requestMatchers(
                                        "/api/orders/**",
                                        "/api/cart/**",
                                        "/api/users/me/**"
                                ).hasRole("CLIENTE")

                                // Admin - Control total
                                .requestMatchers(
                                        "/api/admin/**",
                                        "/api/users/**",
                                        "/api/products/**",
                                        "/api/categories/**",
                                        "/api/orders/all"
                                ).hasRole("ADMIN")

                                // Cualquier otra cosa requiere autenticacion
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}