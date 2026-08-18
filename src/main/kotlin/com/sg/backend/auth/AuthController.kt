package com.sg.backend.auth

import com.sg.backend.auth.dto.LoginRequest
import com.sg.backend.auth.dto.TokenResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Authentication endpoints.
 *   POST /api/auth/login  { username, password } -> { token, ... }
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    @Value("\${app.jwt.expiration-ms}") private val expirationMs: Long,
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): TokenResponse {
        // Throws AuthenticationException on bad credentials -> handled as 401 in ApiExceptionHandler.
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password),
        )
        return TokenResponse(
            token = jwtService.generateToken(request.username),
            expiresInMs = expirationMs,
        )
    }
}
