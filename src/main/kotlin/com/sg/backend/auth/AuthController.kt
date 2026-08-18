package com.sg.backend.auth

import com.sg.backend.auth.dto.LoginRequest
import com.sg.backend.auth.dto.RefreshRequest
import com.sg.backend.auth.dto.TokenResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Authentication endpoints.
 *   POST /api/auth/login    { username, password }  -> { accessToken, refreshToken, ... }
 *   POST /api/auth/refresh  { refreshToken }         -> new token pair (rotation)
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): TokenResponse {
        // Throws AuthenticationException on bad credentials -> 401 (see ApiExceptionHandler).
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password),
        )
        return issueTokens(request.username)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): TokenResponse {
        val claims = jwtService.parse(request.refreshToken)
            ?: throw BadCredentialsException("유효하지 않은 리프레시 토큰입니다.")
        if (!jwtService.isRefreshToken(claims)) {
            throw BadCredentialsException("리프레시 토큰이 아닙니다.")
        }
        // Ensure the user still exists (throws AuthenticationException -> 401 if not).
        userDetailsService.loadUserByUsername(claims.subject)
        return issueTokens(claims.subject)
    }

    /** Rotation: every login/refresh returns a fresh access + refresh pair. */
    private fun issueTokens(username: String) = TokenResponse(
        accessToken = jwtService.generateAccessToken(username),
        refreshToken = jwtService.generateRefreshToken(username),
        accessExpiresInMs = jwtService.accessTtlMs,
        refreshExpiresInMs = jwtService.refreshTtlMs,
    )
}
