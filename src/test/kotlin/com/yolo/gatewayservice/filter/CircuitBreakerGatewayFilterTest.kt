package com.yolo.gatewayservice.filter

import com.yolo.gatewayservice.config.TestScgCircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.concurrent.TimeUnit

@ActiveProfiles(profiles = ["gatewayfilter-circuitbreaker", "circuitbreaker"])
@Import(TestScgCircuitBreakerConfig::class)
@AutoConfigureWebTestClient
@SpringBootTest
class CircuitBreakerGatewayFilterTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
) {

    private lateinit var mockWebServer: MockWebServer
    private val successMessage = "Hello world"
    private val fallbackMessage = "fallback"

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8001)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
        circuitBreakerRegistry.circuitBreaker("halfOpen")
            .reset()
    }

    @Test
    fun circuitBreakerTestCase1() {
        // given
        val slidingWindowSize = 4
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                TimeUnit.MILLISECONDS.sleep(2000)
                return MockResponse()
                    .setBody(successMessage)
            }
        }

        // when, then
        for (i in 0 until slidingWindowSize) {
            webTestClient.get()
                .uri("/hello")
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .isEqualTo(fallbackMessage)
        }

        for (i in 0 until 100) {
            webTestClient.get()
                .uri("/hello")
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .isEqualTo(fallbackMessage)
        }

        assertThat(4).isEqualTo(mockWebServer.requestCount)
    }
}
