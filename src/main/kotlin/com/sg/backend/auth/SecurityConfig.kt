package com.sg.backend.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Stateless JWT security.
 *   - Public:        GET /api/labels, /api/health, POST /api/auth/login
 *   - Authenticated: writes (POST/DELETE /api/labels)
 *
 * Two accounts are seeded from env vars (no public registration):
 *   - admin (ROLE_ADMIN): ADMIN_USERNAME / ADMIN_PASSWORD
 *   - user  (ROLE_USER):  USER_USERNAME / USER_PASSWORD
 */
@Configuration
class SecurityConfig(
    @Value("\${app.admin.username}") private val adminUsername: String,
    @Value("\${app.admin.password}") private val adminPassword: String,
    @Value("\${app.user.username}") private val userUsername: String,
    @Value("\${app.user.password}") private val userPassword: String,
    @Value("\${app.cors.allowed-origins}") private val allowedOrigins: List<String>,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    /** Seeded users (in-memory). Swap for a DB-backed UserDetailsService for multi-user. */
    @Bean
    fun userDetailsService(encoder: PasswordEncoder): UserDetailsService {
        val admin = User.withUsername(adminUsername)
            .password(encoder.encode(adminPassword))
            .roles("ADMIN")
            .build()
        val user = User.withUsername(userUsername)
            .password(encoder.encode(userPassword))
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(admin, user)
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = this@SecurityConfig.allowedOrigins
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", config)
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtService: JwtService, uds: UserDetailsService): SecurityFilterChain {
        val jwtFilter = JwtAuthenticationFilter(jwtService, uds)
        http
            .csrf { it.disable() }
            .cors { } // uses the corsConfigurationSource bean
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/health", "/api/auth/**").permitAll()
                it.requestMatchers(HttpMethod.GET, "/api/labels", "/api/labels/**").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
