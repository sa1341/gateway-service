package com.yolo.gatewayservice.route

import com.yolo.gatewayservice.route.GreetPathRoutePredicateFactory.Config
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.function.Predicate

@Component
class GreetPathRoutePredicateFactory :
    AbstractRoutePredicateFactory<Config>(Config::class.java) {
    class Config {
        var greeting: String? = null
    }

    override fun shortcutFieldOrder(): MutableList<String> {
        return mutableListOf("greeting")
    }

    override fun apply(config: Config?): Predicate<ServerWebExchange> {
        return Predicate<ServerWebExchange> {
            val path = it.request.path.toString()
            val greeting = config?.greeting ?: ""
            path.contains(greeting)
        }
    }
}
