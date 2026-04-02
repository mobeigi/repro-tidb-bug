package io.atlassian.micros.studio.application.database.datasource

import io.atlassian.micros.studio.common.feature.service.FeatureManagementService
import io.atlassian.micros.studio.domain.tcs.service.TcsService
import io.atlassian.micros.studio.domain.tms.service.TmsService
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DataSourceConfig {
    companion object {
        const val DDL_DATASOURCE = "ddlDataSource"
        const val DML_DATASOURCE = "dmlDataSource"
    }

    @Bean(DDL_DATASOURCE)
    fun ddlDataSource(
        tcsService: TcsService,
        tmsService: TmsService,
        metricRegistry: MeterRegistry,
        featureManagementService: FeatureManagementService,
    ): DataSource = DdlWorkspaceAwareRoutingDataSource(tcsService, tmsService, DDL_DATASOURCE, metricRegistry, featureManagementService)

    @Bean(DML_DATASOURCE)
    fun dmlDataSource(
        tcsService: TcsService,
        tmsService: TmsService,
        metricRegistry: MeterRegistry,
        featureManagementService: FeatureManagementService,
    ): DataSource = DmlWorkspaceAwareRoutingDataSource(tcsService, tmsService, DML_DATASOURCE, metricRegistry, featureManagementService)
}
