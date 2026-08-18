package com.sg.backend.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

/**
 * Issues and verifies HS256 JWTs. The signing secret comes from app.jwt.secret
 * (injected via the JWT_SECRET env var in production) and must be at least 32 bytes.
 */
@Service
class JwtService(
    @Value("\${app.jwt.secret}") secret: String,
    @Value("\${app.jwt.expiration-ms}") private val expirationMs: Long,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(username: String): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date(now))
            .expiration(Date(now + expirationMs))
            .signWith(key)
            .compact()
    }

    /** Returns the subject (username) if the token is valid, otherwise null. */
    fun extractUsername(token: String): String? =
        try {
            Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).payload.subject
        } catch (_: Exception) {
            null
        }
}
