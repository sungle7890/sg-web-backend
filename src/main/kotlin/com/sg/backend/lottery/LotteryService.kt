package com.sg.backend.lottery

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * Runs the Python lottery predictor (mini-llm/11_lottery_nn_predict.py) as a subprocess.
 * The script prints `count` lines, each "w w w w w + pb". `count` is passed as its argument.
 *
 * Executable/script paths and limits come from app.lottery.* (env-overridable). Output stays
 * small (max-count lines), so reading the pipe after waitFor does not deadlock.
 */
@Service
class LotteryService(
    @Value("\${app.lottery.python-executable}") private val pythonExecutable: String,
    @Value("\${app.lottery.script-path}") private val scriptPath: String,
    @Value("\${app.lottery.max-count}") private val maxCount: Int,
    @Value("\${app.lottery.timeout-seconds}") private val timeoutSeconds: Long,
) {
    /** Generate `count` tickets (default 1, clamped to [1, max-count]). */
    fun generate(count: Int?): List<String> {
        val n = (count ?: 1).coerceIn(1, maxCount)

        val process = try {
            ProcessBuilder(pythonExecutable, scriptPath, n.toString()).start()
        } catch (e: Exception) {
            throw LotteryException("예측기를 실행할 수 없습니다: ${e.message}")
        }

        if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            throw LotteryException("예측 실행이 시간 내에 끝나지 않았습니다.")
        }

        val stdout = process.inputStream.bufferedReader().readText()
        val stderr = process.errorStream.bufferedReader().readText()
        if (process.exitValue() != 0) {
            throw LotteryException("예측 실행에 실패했습니다: ${stderr.trim().take(300)}")
        }

        val tickets = stdout.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
        if (tickets.isEmpty()) {
            throw LotteryException("예측 결과가 비어 있습니다: ${stderr.trim().take(300)}")
        }
        return tickets
    }
}
