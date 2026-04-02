package io.atlassian.micros.studio.config.spring

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("local")
class RestTemplateConfiguration {
    companion object {
        private val restTemplateBuilder =
            RestTemplateBuilder()
                .rootUri("http://localhost:8081")
    }

    @Bean
    @Primary
    fun testRestTemplate(): TestRestTemplate = TestRestTemplate(restTemplateBuilder)
}
