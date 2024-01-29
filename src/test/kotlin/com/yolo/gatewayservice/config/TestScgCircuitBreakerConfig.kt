package com.yolo.gatewayservice.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestScgCircuitBreakerConfig {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private fun getEventLogger(): Customizer<CircuitBreaker> {
        return Customizer.once({ circuitBreaker ->
            val cbName = circuitBreaker.name
            circuitBreaker.eventPublisher
                .onSuccess {
                    log.info("$cbName success")
                }
                .onError {
                    log.info("$cbName error ${it.throwable}")
                }
                .onStateTransition {
                    log.info("$cbName state changed from ${it.stateTransition.fromState} to ${it.stateTransition.toState}")
                }
                .onSlowCallRateExceeded {
                    log.info("$cbName slow call rate exceeded: ${it.slowCallRate}")
                }
                .onFailureRateExceeded {
                    log.info("$cbName failure rate exceeded: ${it.failureRate}")
                }
        }, CircuitBreaker::getName)
    }

    @Bean
    fun halfOpen(): Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        val targets = arrayOf("halfOpen")
        return Customizer {
            it.addCircuitBreakerCustomizer(
                getEventLogger(),
                *targets,
            )
        }
    }
}
