package com.yolo.gatewayservice.filter

import com.yolo.gatewayservice.filter.AddGreetingPathGatewayFilterFactory.Config
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class AddGreetingPathGatewayFilterFactory :
    AbstractGatewayFilterFactory<Config>(Config::class.java) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    class Config {
        var greeting: String? = null
        var description: String? = null
    }

    override fun shortcutFieldOrder(): MutableList<String> {
        return mutableListOf("greeting", "description")
    }

    override fun apply(config: Config?): GatewayFilter {
        log.info("description: ${config?.description ?: "no found value"}")
        return GatewayFilter { exchange, chain ->
            val path = exchange.request.path.value()
            val nextReq = exchange.request
                .mutate()
                .path("/${config?.greeting}$path")
                .build()

            val next = exchange.mutate()
                .request(nextReq).build()
            chain.filter(next)
        }
    }
}
