package io.atlassian.micros.studio.application.database.datasource

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

/**
 * For integration tests, DDL and DML are basically equivalent.
 */
@TestConfiguration
class IntegrationTestDataSourceConfig {

    companion object {
        private const val DB_HOST = "localhost"
        private const val DB_PORT = 4000
        private const val DB_USER = "root"
        private const val DB_PASSWORD = ""

        const val TEST_DB_NAME = "test"
        const val FLYWAY_DB_NAME = "schema_validation_flyway"
        const val EXPOSED_DB_NAME = "schema_validation_exposed"

        const val DDL_DATASOURCE = "ddlDataSource"
        const val DML_DATASOURCE = "dmlDataSource"
        const val SCHEMA_VALIDATION_FLYWAY_DDL_DATASOURCE = "schemaValidationFlywayDdlDataSource"
        const val SCHEMA_VALIDATION_FLYWAY_DML_DATASOURCE = "schemaValidationFlywayDmlDataSource"
        const val SCHEMA_VALIDATION_EXPOSED_DDL_DATASOURCE = "schemaValidationExposedDdlDataSource"
        const val SCHEMA_VALIDATION_EXPOSED_DML_DATASOURCE = "schemaValidationExposedDmlDataSource"
    }

    @Bean
    @Primary
    @Qualifier(DDL_DATASOURCE)
    fun testDdlDataSource(): DataSource =
        DataSourceBuilder
            .create()
            .url("jdbc:mysql://$DB_HOST:$DB_PORT/$TEST_DB_NAME")
            .username(DB_USER)
            .password(DB_PASSWORD)
            .build()

    @Bean
    @Primary
    @Qualifier(DML_DATASOURCE)
    fun testDmlDataSource(): DataSource =
        DataSourceBuilder
            .create()
            .url("jdbc:mysql://$DB_HOST:$DB_PORT/$TEST_DB_NAME")
            .username(DB_USER)
            .password(DB_PASSWORD)
            .build()

    @Bean
    @Qualifier(SCHEMA_VALIDATION_FLYWAY_DDL_DATASOURCE)
    fun schemaValidationFlywayDdlDataSource(): DataSource =
        DataSourceBuilder
            .create()
            .url("jdbc:mysql://$DB_HOST:$DB_PORT/$FLYWAY_DB_NAME")
            .username(DB_USER)
            .password(DB_PASSWORD)
            .build()

    @Bean
    @Qualifier(SCHEMA_VALIDATION_FLYWAY_DML_DATASOURCE)
    fun schemaValidationFlywayDmlDataSource(): DataSource =
        DataSourceBuilder
            .create()
            .url("jdbc:mysql://$DB_HOST:$DB_PORT/$FLYWAY_DB_NAME")
            .username(DB_USER)
            .password(DB_PASSWORD)
            .build()

    @Bean
    @Qualifier(SCHEMA_VALIDATION_EXPOSED_DDL_DATASOURCE)
    fun schemaValidationExposedDdlDataSource(): DataSource =
        DataSourceBuilder
            .create()
            .url("jdbc:mysql://$DB_HOST:$DB_PORT/$EXPOSED_DB_NAME")
            .username(DB_USER)
            .password(DB_PASSWORD)
            .build()

    @Bean
    @Qualifier(SCHEMA_VALIDATION_EXPOSED_DML_DATASOURCE)
    fun schemaValidationExposedDmlDataSource(): DataSource =
        DataSourceBuilder
            .create()
            .url("jdbc:mysql://$DB_HOST:$DB_PORT/$EXPOSED_DB_NAME")
            .username(DB_USER)
            .password(DB_PASSWORD)
            .build()
}
