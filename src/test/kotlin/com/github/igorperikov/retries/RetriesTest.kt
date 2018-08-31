package com.github.igorperikov.retries

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object RetriesSpek : Spek({
    Feature("Any-exception retry") {
        Scenario("Block of code doesn't throw exception") {
            var executions = 0

            When("Executing") {
                retry(retries = 3) {
                    executions++
                }
            }

            Then("Block of code executed only once") {
                assertThat(executions, equalTo(1))
            }
        }

        Scenario("Block of code throws exception") {
            val retries = 3
            var executions = 0

            When("Executing silently") {
                retry(retries = retries, failSilently = true) {
                    executions++
                    throw RuntimeException()
                }
            }

            Then("Block of code executes $retries times") {
                assertThat(executions, equalTo(retries))
            }
        }
    }
})
