package com.sg.backend.lottery

/** Thrown when the lottery predictor subprocess fails (-> HTTP 500). */
class LotteryException(message: String) : RuntimeException(message)
