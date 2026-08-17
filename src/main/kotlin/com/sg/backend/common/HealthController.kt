package com.sg.backend.common

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/** Simple health check endpoint: GET /api/health */
@RestController
class HealthController {

    @GetMapping("/api/health")
    fun health(): Map<String, String> = mapOf("status" to "UP")
}
