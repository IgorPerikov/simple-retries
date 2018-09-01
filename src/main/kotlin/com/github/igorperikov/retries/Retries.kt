package com.github.igorperikov.retries

import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

/**
 * Retry [block] code [retries] times.
 * Retry executes immediately on any [Exception].
 * [failSilently]: if true then [RetriesExceedException] would not be thrown after failing all retries.
 * [exceptions] specifies which exceptions should lead to retry.
 */
fun retry(
    retries: Int = 3,
    exceptions: Set<KClass<out Exception>> = setOf(Exception::class),
    failSilently: Boolean = true,
    block: () -> Unit
) {
    if (retries <= 0) throw IllegalArgumentException("Retries should be positive number, got=$retries")
    for (retriesRemained in retries - 1 downTo 0) {
        try {
            block()
            break
        } catch (e: Exception) {
            if (!exceptionSupported(e, exceptions)) break
            if (retriesRemained == 0 && !failSilently) throw RetriesExceedException()
        }
    }
}

private fun exceptionSupported(e: Exception, exceptions: Set<KClass<out Exception>>): Boolean {
    val eClass = e::class
    for (exception in exceptions) {
        if (exception.isSuperclassOf(eClass)) return true
    }
    return false
}

class RetriesExceedException : RuntimeException()
