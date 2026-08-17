package com.sg.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * CORS configuration. Allows requests from the frontend (Vite dev server)
 * during development. Allowed origins are managed via app.cors.allowed-origins
 * in application.properties.
 *
 * Note: if cookie-based authentication is adopted later, allowCredentials(true)
 * plus explicit origins will be required.
 */
@Configuration
class WebCorsConfig(
    @Value("\${app.cors.allowed-origins}") private val allowedOrigins: List<String>,
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins.toTypedArray())
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            // Change to true if cookie/auth headers become necessary
            .allowCredentials(false)
    }
}
