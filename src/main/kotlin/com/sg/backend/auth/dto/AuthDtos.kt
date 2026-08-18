package com.sg.backend.auth.dto

/** Login request body */
data class LoginRequest(
    val username: String = "",
    val password: String = "",
)

/** Token response returned on successful login */
data class TokenResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresInMs: Long,
)
