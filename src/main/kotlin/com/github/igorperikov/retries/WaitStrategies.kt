package com.github.igorperikov.retries

import java.lang.Thread.sleep

interface WaitStrategy {
    fun waitNextRetry(attempt: Int)
}

class ImmediateRetryStrategy : WaitStrategy {
    override fun waitNextRetry(attempt: Int) {}
}

class MonotonouslyIncreasingWaitIntervalStrategy(
    private val initWaitMs: Long,
    private val step: Long = initWaitMs
) : WaitStrategy {
    override fun waitNextRetry(attempt: Int) {
        sleep(initWaitMs + step * (attempt - 1))
    }
}
