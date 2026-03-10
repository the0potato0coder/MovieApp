package com.moviebookingapp.movie_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Industry standard for hashing passwords
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF and CORS using the new Lambda DSL syntax
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // 2. Set up our URL routing rules (Note: antMatchers is now requestMatchers)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Public Endpoints: Registration, Login, Forgot Password, View All, Search
                        .requestMatchers("/api/v1.0/moviebooking/register", "/api/v1.0/moviebooking/login").permitAll()
                        .requestMatchers("/api/v1.0/moviebooking/all").permitAll()
                        .requestMatchers("/api/v1.0/moviebooking/movies/search/**").permitAll()
                        .requestMatchers("/api/v1.0/moviebooking/forgot-password").permitAll()
                        .requestMatchers("/api/v1.0/moviebooking/reset-password").permitAll()

                        // Admin Endpoints: Update Ticket Status and Delete Movie
                        .requestMatchers(HttpMethod.PUT, "/api/v1.0/moviebooking/*/update/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1.0/moviebooking/*/delete/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1.0/moviebooking/admin/status/**").hasRole("ADMIN")

                        // Secured Endpoints: Booking a ticket requires the user to be logged in
                        .requestMatchers(HttpMethod.POST, "/api/v1.0/moviebooking/*/add").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )

                // 3. Tell Spring NOT to save sessions in memory (make it completely stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // 4. Register our custom JWT filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Allow your React Vite local server
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 2. Allow all the HTTP methods the rubric requires
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Allow headers, specifically the Authorization header for our JWT token
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this rule to all API endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
