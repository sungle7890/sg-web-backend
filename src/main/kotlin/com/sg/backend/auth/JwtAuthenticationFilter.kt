package com.sg.backend.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Reads a "Authorization: Bearer <token>" header, validates the JWT, and populates
 * the SecurityContext. Only ACCESS tokens are accepted here — a refresh token cannot
 * be used to authorize API requests. Not a Spring bean on purpose (instantiated in
 * SecurityConfig) to avoid Boot auto-registering it as a second servlet filter.
 */
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val claims = jwtService.parse(header.substring(7))
            if (claims != null && jwtService.isAccessToken(claims) &&
                SecurityContextHolder.getContext().authentication == null
            ) {
                runCatching { userDetailsService.loadUserByUsername(claims.subject) }.getOrNull()?.let { userDetails ->
                    val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
