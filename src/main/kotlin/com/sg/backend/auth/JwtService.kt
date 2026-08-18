package com.sg.backend.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

/**
 * Issues and verifies HS-signed JWTs. Two token types are distinguished by the
 * "typ" claim: short-lived access tokens (for API calls) and long-lived refresh
 * tokens (only for /api/auth/refresh). The signing secret comes from app.jwt.secret
 * (JWT_SECRET env var in production) and must be at least 32 bytes.
 */
@Service
class JwtService(
    @Value("\${app.jwt.secret}") secret: String,
    @Value("\${app.jwt.access-expiration-ms}") val accessTtlMs: Long,
    @Value("\${app.jwt.refresh-expiration-ms}") val refreshTtlMs: Long,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateAccessToken(username: String): String = build(username, TYPE_ACCESS, accessTtlMs)

    fun generateRefreshToken(username: String): String = build(username, TYPE_REFRESH, refreshTtlMs)

    private fun build(username: String, type: String, ttlMs: Long): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(username)
            .claim(CLAIM_TYPE, type)
            .issuedAt(Date(now))
            .expiration(Date(now + ttlMs))
            .signWith(key)
            .compact()
    }

    /** Verify signature/expiry and return claims, or null if the token is invalid. */
    fun parse(token: String): Claims? =
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        } catch (_: Exception) {
            null
        }

    fun isAccessToken(claims: Claims): Boolean = claims[CLAIM_TYPE] == TYPE_ACCESS
    fun isRefreshToken(claims: Claims): Boolean = claims[CLAIM_TYPE] == TYPE_REFRESH

    companion object {
        private const val CLAIM_TYPE = "typ"
        private const val TYPE_ACCESS = "access"
        private const val TYPE_REFRESH = "refresh"
    }
}
