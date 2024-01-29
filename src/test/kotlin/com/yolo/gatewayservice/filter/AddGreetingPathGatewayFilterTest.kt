package com.yolo.gatewayservice.filter

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

@ActiveProfiles("gatewayfilter-greeting")
@AutoConfigureWebTestClient
@SpringBootTest
class AddGreetingPathGatewayFilterTest @Autowired constructor(
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
    fun gatewayFilterTest() {
        // given
        mockWebServer.enqueue(MockResponse())

        // when
        webTestClient.get()
            .uri("/abc/def")
            .exchange()
            .expectStatus().isOk

        val request = mockWebServer.takeRequest()
        val path = request.path

        // then
        assertThat(path).isEqualTo("/hello/abc/def")
    }
}
