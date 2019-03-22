package com.test.jl

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class Application {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}