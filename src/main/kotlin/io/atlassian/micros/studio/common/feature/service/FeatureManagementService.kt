package io.atlassian.micros.studio.common.feature.service

import io.atlassian.micros.studio.common.CollaborationContextId
import io.atlassian.micros.studio.common.OrganizationId
import io.atlassian.micros.studio.common.WorkspaceId
import io.atlassian.micros.studio.common.feature.model.FeatureGate
import org.springframework.stereotype.Service

/**
 * Service for managing feature gates in the Studio application.
 *
 * This is a stub implementation that always returns the feature gate's fallback value.
 * It replaces the Atlassian-internal Statsig-based implementation to allow the repo
 * to build from open-source dependencies only.
 */
@Service
class FeatureManagementService {
    /**
     * Checks if a feature gate is enabled.
     * Always returns the gate's configured fallback value.
     */
    fun isFeatureEnabled(
        featureGate: FeatureGate,
        workspaceId: WorkspaceId?,
        accountId: String?,
    ): Boolean = featureGate.fallback

    /**
     * Checks if a feature gate is enabled.
     * Always returns the gate's configured fallback value.
     */
    fun isFeatureEnabled(
        featureGate: FeatureGate,
        collaborationContextId: CollaborationContextId,
    ): Boolean = featureGate.fallback

    /**
     * Checks if a feature gate is enabled for an org.
     * Always returns the gate's configured fallback value.
     */
    fun isFeatureEnabledForOrg(
        featureGate: FeatureGate,
        orgId: OrganizationId,
    ): Boolean = featureGate.fallback
}
