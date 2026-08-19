package com.sg.backend.lottery

import com.sg.backend.lottery.dto.LotteryResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Lottery prediction API. Requires authentication (not in the SecurityConfig allow-list).
 *   GET /api/lottery?count=N   -> N predicted tickets (count optional, default 1)
 */
@RestController
@RequestMapping("/api/lottery")
class LotteryController(
    private val service: LotteryService,
) {
    @GetMapping
    fun lottery(@RequestParam(required = false) count: Int?): LotteryResponse {
        val tickets = service.generate(count)
        return LotteryResponse(count = tickets.size, tickets = tickets)
    }
}
