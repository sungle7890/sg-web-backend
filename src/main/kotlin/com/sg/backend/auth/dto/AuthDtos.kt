package com.sg.backend.auth.dto

/** Login request body */
data class LoginRequest(
    val username: String = "",
    val password: String = "",
)

/** Refresh request body */
data class RefreshRequest(
    val refreshToken: String = "",
)

/** Token pair returned on login and refresh */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val accessExpiresInMs: Long,
    val refreshExpiresInMs: Long,
)
