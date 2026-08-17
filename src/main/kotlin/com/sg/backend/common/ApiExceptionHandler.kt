package com.sg.backend.common

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global exception handling. Normalizes error responses into a standard
 * format (RFC 7807 ProblemDetail). As features grow, extend exception
 * handling here consistently.
 */
@RestControllerAdvice
class ApiExceptionHandler {

    /** Invalid input -> 400 */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "잘못된 요청입니다.")

    /** Resource not found -> 404 */
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "리소스를 찾을 수 없습니다.")
}
