package io.atlassian.micros.studio.common.feature.model

data class FeatureGate(
    val gateKey: String,
    val fallback: Boolean = false,
)
