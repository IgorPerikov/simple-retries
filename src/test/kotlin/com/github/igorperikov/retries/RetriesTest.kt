package com.github.igorperikov.retries

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class RetriesTest {
    private val defaultAttempts = DEFAULT_ATTEMPTS

    @Test
    fun `retryer configured with non-default attempts executes body proper amount of times`() {
        var executions = 0
        val attempts = 5
        retry(attempts) {
            executions++
            throw RuntimeException()
        }
        assertThat(executions, equalTo(attempts))
    }

    @Test
    fun `retryer cannot be configured with zero or less retry attempts`() {
        assertThrows<IllegalArgumentException> {
            retry(0) {  }
        }
        assertThrows<IllegalArgumentException> {
            retry(-1) {  }
        }
    }

    @Test
    fun `exceptions-free code should be executed once`() {
        var executions = 0
        retry(defaultAttempts) {
            executions++
        }
        assertThat(executions, equalTo(1))
    }

    @Test
    fun `any exception should trigger attempts in default configuration`() {
        var executions = 0
        retry(defaultAttempts) {
            executions++
            when (executions) {
                1 -> throw RuntimeException()
                2 -> throw IOException()
                else -> throw Exception()
            }
        }
        assertThat(executions, equalTo(defaultAttempts))
    }

    @Test
    fun `failed execution in non-silent mode should complete with RetriesExceedException`() {
        assertThrows<RetriesExceedException> {
            retry(silent = false) {
                throw RuntimeException()
            }
        }
    }

    @Test
    fun `when exception provided, throwing subclass exception should trigger retry`() {
        var executions = 0
        retry(defaultAttempts, setOf(ParentException::class)) {
            executions++
            throw ChildException()
        }
        assertThat(executions, equalTo(defaultAttempts))
    }

    @Test
    fun `when exception provided, throwing this exception should trigger retry`() {
        var executions = 0
        retry(defaultAttempts, setOf(ParentException::class)) {
            executions++
            throw ParentException()
        }
        assertThat(executions, equalTo(defaultAttempts))
    }

    @Test
    fun `when exception provided, throwing exception out of it's hierarchy should not trigger retry`() {
        var executions = 0
        retry(defaultAttempts, setOf(IOException::class)) {
            executions++
            throw IllegalStateException()
        }
        assertThat(executions, equalTo(1))
    }

    @Test
    fun `when retryes runs out of attempts should execute fallback if provided`() {
        var fallbackExecutions = 0
        retry(fallback = { fallbackExecutions++ }) {
            throw RuntimeException()
        }
        assertThat(fallbackExecutions, equalTo(1))
    }
}
