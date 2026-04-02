package io.atlassian.micros.studio.testsupport

import io.atlassian.micros.studio.application.database.datasource.IntegrationTestDataSourceConfig
import io.atlassian.micros.studio.application.database.exposed.IntegrationTestExposedConfig
import io.atlassian.micros.studio.config.LocalRestSecurityConfig
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = [])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = ["classpath:nebulae-integration-test.properties"])
@Tag("integration-test")
@Target(AnnotationTarget.CLASS)
@AutoConfigureMockMvc
@Import(IntegrationTestDataSourceConfig::class, IntegrationTestExposedConfig::class, LocalRestSecurityConfig::class)
annotation class IntegrationTest
