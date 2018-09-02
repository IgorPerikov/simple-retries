package com.github.igorperikov.retries

import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

@PublishedApi
internal const val DEFAULT_ATTEMPTS = 3

/**
 * Retry [block] code [attempts] times.
 * Retry executes immediately on any [Exception].
 * Exceptions which should lead to retry can be specified via [exceptions].
 * When retryer runs out of attempts, executes [fallback],
 * if fallback wasn't provided and [silent] mode disabled - throws [RetriesExceedException] with actual cause.
 */
inline fun retry(
    attempts: Int = DEFAULT_ATTEMPTS,
    exceptions: Set<KClass<out Exception>> = setOf(Exception::class),
    waitStrategy: WaitStrategy = ImmediateRetryStrategy(),
    silent: Boolean = true,
    noinline fallback: (() -> Unit)? = null,
    block: () -> Unit
) {
    require(attempts > 0) { "Attempts should be positive number, got=$attempts" }
    for (attempt in 1..attempts) {
        try {
            block()
            break
        } catch (e: Exception) {
            if (exceptionNotSupported(e, exceptions)) break
            if (attempt == attempts) {
                if (fallback == null) {
                    if (!silent) {
                        throw RetriesExceedException(e)
                    }
                } else {
                    fallback()
                }
            }
        }
        waitStrategy.waitNextRetry(attempt)
    }
}

@PublishedApi
internal fun exceptionNotSupported(e: Exception, supportedExceptions: Set<KClass<out Exception>>): Boolean {
    val thrownException = e::class
    for (supportedException in supportedExceptions) {
        if (supportedException.isSuperclassOf(thrownException)) return false
    }
    return true
}

class RetriesExceedException(cause: Throwable) : RuntimeException(cause)
