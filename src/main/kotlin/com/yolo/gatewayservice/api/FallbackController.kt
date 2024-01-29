package com.yolo.gatewayservice.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/fallback")
@RestController
class FallbackController {
    @GetMapping("/hello")
    fun hello() = "fallback"
}
