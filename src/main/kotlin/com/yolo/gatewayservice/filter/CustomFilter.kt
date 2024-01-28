package com.yolo.gatewayservice.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CustomFilter : AbstractGatewayFilterFactory<CustomFilter.Config>(Config::class.java) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val response = exchange.response
            chain.filter(exchange).then(Mono.fromRunnable { log.info("CustomFilter response status code: ${response.statusCode}") })
        }
    }

    class Config
}

@Component
class GlobalFilter : AbstractGatewayFilterFactory<GlobalFilter.Config>(Config::class.java) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val response = exchange.response

            log.info("Global request id: ${request.id}")
            chain.filter(exchange).then(
                Mono.fromRunnable {
                    log.info("Global response status code: ${response.statusCode}")
                },
            )
        }
    }

    class Config
}
