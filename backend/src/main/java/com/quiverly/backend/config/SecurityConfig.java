package com.quiverly.backend.config;

import com.quiverly.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // ok for API + Postman; for browsers enable with cookies
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        // public endpoints (match your controllers)
                        .requestMatchers("/api/auth/**").permitAll() //for login
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/**").permitAll()        // registration
                        .requestMatchers(HttpMethod.GET, "/api/v1/surfboard/**").permitAll() //public surfboard list maybe?
                        // authenticated user endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/surfboard/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/surfboard/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/surfboard/**").authenticated()

                        // admin only hehe thats me lol 0_0
                        .requestMatchers("/api/v1/user/**").hasRole("ADMIN")
                        //all else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}