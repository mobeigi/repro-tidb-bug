package io.atlassian.micros.studio.common.feature.service

import io.atlassian.micros.studio.common.feature.model.FeatureGate

object FeatureGates {
    val ENABLE_SPACE_CREATE = FeatureGate("asf-243-enable-space-create", false)
    val ENABLE_CC_BASED_TENANTS = FeatureGate("asf-492-enable-cc-based-tenants", false)
    val SITES_IN_WORKSPACE_CONTEXT = FeatureGate("asf-788-sites-in-workspace-context", false)
    val ENABLE_ANALYTICS = FeatureGate("asf-775-set-up-analytics", false)
    val ENABLE_DEFAULT_PERMISSIONS = FeatureGate("asf-686-default-permissions", false)
    val JIRA_CORE_UAM_ALLOWLIST = FeatureGate("asf-948-jira-core-uam-allowlist", false)
    val ENABLE_PERMS_ANALYTICS = FeatureGate("asf-887-perms-analytics", false)
    val SORT_USER_CLASSES = FeatureGate("asf-1045-sort-user-classes", false)
    val SKIP_ELIGIBILITY_CHECK = FeatureGate("asf-1053-skip-eligibility-check", false)
    val FIX_DOUBLE_IDENTITY_CALL = FeatureGate("asf-1098-fix-double-identity-call", false)
    val DEDUP_JDBC = FeatureGate("asf-1122-dedup-jdbc", false)
    val UPDATE_ROVO_ELIGIBILITY_CHECK = FeatureGate("asf-1100-update-rovo-eligibility-check", false)
    val UAM_POST_FLIGHT_TIMEOUT_INCREASE = FeatureGate("asf-1105-increase-wait-time-for-uam-post-flight", false)
    val ROLLBACK_REQUEST_FG = FeatureGate("asf-1124-handle-cp-rollback-request")
    val MARK_POST_FLIGHT_FAIL_AS_SUCCESS = FeatureGate("asf-1142-mark-failed-post-flight-as-successful")
    val RUN_OPT_IN_FLOW_ASYNC = FeatureGate("asf-1148-run-opt-in-flow-async")
    val SKIP_VORTEX_ELIGIBILITY_CHECK = FeatureGate("asf-1202-skip-vortex-eligibility-check", false)
    val CROSS_ORG_ENUMERATION_FIX = FeatureGate("asf-1210-cross-org-enumeration-fix")
}
