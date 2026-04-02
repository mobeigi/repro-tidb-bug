package io.atlassian.micros.studio.application.database.exposed

import io.atlassian.micros.studio.application.database.datasource.IntegrationTestDataSourceConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import javax.sql.DataSource

@TestConfiguration
class IntegrationTestExposedConfig {
    companion object {
        const val SCHEMA_VALIDATION_FLYWAY_DDL_DATABASE = "schemaValidationFlywayDdlExposedDatabase"
        const val SCHEMA_VALIDATION_FLYWAY_DML_DATABASE = "schemaValidationFlywayDmlExposedDatabase"
        const val SCHEMA_VALIDATION_EXPOSED_DDL_DATABASE = "schemaValidationExposedDdlExposedDatabase"
        const val SCHEMA_VALIDATION_EXPOSED_DML_DATABASE = "schemaValidationExposedDmlExposedDatabase"
    }

    @Bean(SCHEMA_VALIDATION_FLYWAY_DDL_DATABASE)
    fun schemaValidationFlywayDdlExposedDatabase(
        @Qualifier(IntegrationTestDataSourceConfig.SCHEMA_VALIDATION_FLYWAY_DDL_DATASOURCE) dataSource: DataSource,
    ): Database = Database.connect(dataSource)

    @Bean(SCHEMA_VALIDATION_FLYWAY_DML_DATABASE)
    fun schemaValidationFlywayDmlExposedDatabase(
        @Qualifier(IntegrationTestDataSourceConfig.SCHEMA_VALIDATION_FLYWAY_DML_DATASOURCE) dataSource: DataSource,
    ): Database = Database.connect(dataSource)

    @Bean(SCHEMA_VALIDATION_EXPOSED_DDL_DATABASE)
    fun schemaValidationExposedDdlExposedDatabase(
        @Qualifier(IntegrationTestDataSourceConfig.SCHEMA_VALIDATION_EXPOSED_DDL_DATASOURCE) dataSource: DataSource,
    ): Database = Database.connect(dataSource)

    @Bean(SCHEMA_VALIDATION_EXPOSED_DML_DATABASE)
    fun schemaValidationExposedDmlExposedDatabase(
        @Qualifier(IntegrationTestDataSourceConfig.SCHEMA_VALIDATION_EXPOSED_DML_DATASOURCE) dataSource: DataSource,
    ): Database = Database.connect(dataSource)
}
