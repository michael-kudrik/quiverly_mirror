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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        try {
            return authenticationConfiguration.getAuthenticationManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) {
        try {
            http
                    .cors(org.springframework.security.config.Customizer.withDefaults()) // Enable CORS!
                    .csrf(AbstractHttpConfigurer::disable) // ok for API + Postman; for browsers enable with cookies
                    .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                    .authorizeHttpRequests(auth -> auth
                            // public endpoints (match your controllers)
                            .requestMatchers("/api/auth/**").permitAll() //for login
                            .requestMatchers(HttpMethod.POST, "/api/v1/user/**").permitAll()        // registration
                            // authenticated user endpoints
                            .requestMatchers(HttpMethod.GET, "/api/v1/surfboard/my-boards").authenticated()
                            //public surfboard list maybe?
                            .requestMatchers(HttpMethod.POST, "/api/v1/surfboard/**").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/v1/surfboard/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/surfboard/**").authenticated()

                            .requestMatchers(HttpMethod.GET, "/api/v1/surfboard/**").permitAll() //public surfboard list maybe?
                            // admin only hehe that's me lol 0_0
                            .requestMatchers("/api/v1/user/**").hasRole("ADMIN")
                            // allow access of uploads directory
                            .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                            //all else requires authentication
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);


            return http.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}