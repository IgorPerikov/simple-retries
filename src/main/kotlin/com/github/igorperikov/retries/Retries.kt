package com.github.igorperikov.retries

import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

/**
 * Retry [block] code [attempts] times.
 * Retry executes immediately on any [Exception].
 * [exceptions] specifies which exceptions should lead to retry.
 * When retryer runs out of attempts, executes [fallback],
 * if fallback does not provided and [silent] mode disabled - throws [RetriesExceedException] with proper cause
 */
fun retry(
    attempts: Int = 3,
    exceptions: Set<KClass<out Exception>> = setOf(Exception::class),
    silent: Boolean = true,
    fallback: (() -> Unit)? = null,
    block: () -> Unit
) {
    require(attempts > 0) { "Retry attempts should be positive number, got=$attempts" }
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
    }
}

private fun exceptionNotSupported(e: Exception, supportedExceptions: Set<KClass<out Exception>>): Boolean {
    val thrownException = e::class
    for (supportedException in supportedExceptions) {
        if (supportedException.isSuperclassOf(thrownException)) return false
    }
    return true
}

class RetriesExceedException(cause: Throwable) : RuntimeException(cause)

fun uncoveredFunction() {
    println("doing something uncovered")
}
