package com.sg.backend.lottery.dto

/** Lottery prediction response. Each ticket is a string like "3 11 15 22 33 + 7". */
data class LotteryResponse(
    val count: Int,
    val tickets: List<String>,
)
