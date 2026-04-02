package io.atlassian.micros.studio.application.database.exposed

import io.atlassian.micros.studio.application.database.datasource.DataSourceConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class ExposedConfig {
    companion object {
        const val DDL_DATABASE = "ddlExposedDatabase"
        const val DML_DATABASE = "dmlExposedDatabase"
    }

    @Bean(DDL_DATABASE)
    fun ddlExposedDatabase(
        @Qualifier(DataSourceConfig.DDL_DATASOURCE) dataSource: DataSource,
    ): Database = Database.connect(dataSource)

    @Bean(DML_DATABASE)
    fun dmlExposedDatabase(
        @Qualifier(DataSourceConfig.DML_DATASOURCE) dataSource: DataSource,
    ): Database = Database.connect(dataSource)
}
