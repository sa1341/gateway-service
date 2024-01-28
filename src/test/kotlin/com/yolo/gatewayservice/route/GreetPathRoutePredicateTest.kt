package com.yolo.gatewayservice.route

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("greet-predicate")
@AutoConfigureWebTestClient
@SpringBootTest
class GreetPathRoutePredicateTest @Autowired constructor(
    private val webTestClient: WebTestClient,
) {

    lateinit var mockWebServer: MockWebServer

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8001)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun helloTest() {
        // given
        val message = "Hello world"
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(message),
        )

        // when, then
        webTestClient.get()
            .uri("/hello")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java)
            .isEqualTo(message)

        val recordedRequest = mockWebServer.takeRequest()

        val expectedPath = "/hello"
        assertThat(expectedPath).isEqualTo(recordedRequest.path)
    }

    @Test
    fun holaTest() {
        // given
        val message = "Hola world"
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(message),
        )

        // when, then
        webTestClient.get()
            .uri("/hola")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java)
            .isEqualTo(message)

        val recordedRequest = mockWebServer.takeRequest()

        val expectedPath = "/hola"
        assertThat(expectedPath).isEqualTo(recordedRequest.path)
    }

    @Test
    fun notFound() {
        // when, then
        webTestClient.get()
            .uri("/hi")
            .exchange()
            .expectStatus()
            .isNotFound

        assertThat(0).isEqualTo(mockWebServer.requestCount)
    }
}
