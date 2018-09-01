package com.github.igorperikov.retries

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class RetriesTest {
    private val retries = 3

    @Test
    fun `exceptions-free code should be executed once`() {
        var executions = 0
        retry(retries) {
            executions++
        }
        assertThat(executions, equalTo(1))
    }

    @Test
    fun `any exception should trigger retries in default configuration`() {
        var executions = 0
        retry(retries) {
            executions++
            when (executions) {
                1 -> throw IllegalArgumentException()
                2 -> throw IOException()
                else -> throw RuntimeException()
            }
        }
        assertThat(executions, equalTo(retries))
    }

    @Test
    fun `non-silent execution should complete with RetriesExceedException`() {
        assertThrows<RetriesExceedException> {
            retry(failSilently = false) {
                throw RuntimeException()
            }
        }
    }

    @Test
    fun `when exception provided, throwing subclass exception should trigger retry`() {
        var executions = 0
        retry(retries, setOf(ParentException::class)) {
            executions++
            throw ChildException()
        }
        assertThat(executions, equalTo(retries))
    }

    @Test
    fun `when exception provided, throwing this exception should trigger retry`() {
        var executions = 0
        retry(retries, setOf(ParentException::class)) {
            executions++
            throw ParentException()
        }
        assertThat(executions, equalTo(retries))
    }

    @Test
    fun `when exception provided, throwing exception out of it's hierarchy should not trigger retry`() {
        var executions = 0
        retry(retries, setOf(IllegalArgumentException::class)) {
            executions++
            throw IllegalStateException()
        }
        assertThat(executions, equalTo(1))
    }
}
