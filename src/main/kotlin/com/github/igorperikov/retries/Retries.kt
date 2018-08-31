@file:JvmName("Retries")

package com.github.igorperikov.retries

/**
 * Retry [block] code [retries] times.
 * Retry executes immediately on any [Exception].
 * [failSilently]: if true then [RetriesExceedException] would not be thrown after failing all retries.
 */
fun retry(retries: Int = 3, failSilently: Boolean = false, block: () -> Unit) {
    if (retries <= 0) throw IllegalArgumentException("Retries should be positive number, got=$retries")
    for (retriesRemained in retries - 1 downTo 0) {
        try {
            block()
            break
        } catch (e: Exception) {
            if (retriesRemained == 0 && !failSilently) throw RetriesExceedException()
        }
    }
}

class RetriesExceedException : RuntimeException()
