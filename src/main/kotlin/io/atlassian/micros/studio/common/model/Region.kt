package io.atlassian.micros.studio.common.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Region and environment mapping as defined in Micros documentation.
 *
 * See: [Micros – Environments, regions, and domains](https://hello.atlassian.net/wiki/x/OXf3CQ)
 */
enum class MicrosEnvironment(
    val value: String,
    val environment: Environment,
    val awsRegion: AwsRegion,
) {
    // Production
    PROD_EAST("prod-east", Environment.PRODUCTION, AwsRegion.US_EAST_1),
    PROD_WEST("prod-west", Environment.PRODUCTION, AwsRegion.US_WEST_1),
    PROD_WEST2("prod-west2", Environment.PRODUCTION, AwsRegion.US_WEST_2),
    PROD_CACENTRAL("prod-cacentral", Environment.PRODUCTION, AwsRegion.CA_CENTRAL_1),
    PROD_EUWEST("prod-euwest", Environment.PRODUCTION, AwsRegion.EU_WEST_1),
    PROD_EUWEST2("prod-euwest2", Environment.PRODUCTION, AwsRegion.EU_WEST_2),
    PROD_EUCENTRAL("prod-eucentral", Environment.PRODUCTION, AwsRegion.EU_CENTRAL_1),
    PROD_EUCENTRAL2("prod-eucentral2", Environment.PRODUCTION, AwsRegion.EU_CENTRAL_2),
    PROD_APSE("prod-apse", Environment.PRODUCTION, AwsRegion.AP_SOUTHEAST_1),
    PROD_APSE2("prod-apse2", Environment.PRODUCTION, AwsRegion.AP_SOUTHEAST_2),
    PROD_APNE1("prod-apne1", Environment.PRODUCTION, AwsRegion.AP_NORTHEAST_1),
    PROD_APNE2("prod-apne2", Environment.PRODUCTION, AwsRegion.AP_NORTHEAST_2),
    PROD_APSOUTH("prod-apsouth", Environment.PRODUCTION, AwsRegion.AP_SOUTH_1),
    PROD_FEDM_EAST("prod-fedm-east", Environment.PRODUCTION, AwsRegion.US_EAST_1),
    PROD_FEDM_WEST2("prod-fedm-west2", Environment.PRODUCTION, AwsRegion.US_WEST_2),

    // Staging
    STG_EAST("stg-east", Environment.STAGING, AwsRegion.US_EAST_1),
    STG_WEST2("stg-west2", Environment.STAGING, AwsRegion.US_WEST_2),
    STG_EUCENTRAL("stg-eucentral", Environment.STAGING, AwsRegion.EU_CENTRAL_1),
    STG_EUCENTRAL2("stg-eucentral2", Environment.STAGING, AwsRegion.EU_CENTRAL_2),
    STG_APSE2("stg-apse2", Environment.STAGING, AwsRegion.AP_SOUTHEAST_2),
    STG_FEDM_EAST("stg-fedm-east", Environment.STAGING, AwsRegion.US_EAST_1),
    STG_FEDM_WEST2("stg-fedm-west2", Environment.STAGING, AwsRegion.US_WEST_2),

    // Development
    DDEV("ddev", Environment.DEVELOPMENT, AwsRegion.AP_SOUTHEAST_2),
    DEV_WEST2("dev-west2", Environment.DEVELOPMENT, AwsRegion.US_WEST_2),
    ADEV("adev", Environment.DEVELOPMENT, AwsRegion.AP_SOUTHEAST_2),
    ADEV_WEST2("adev-west2", Environment.DEVELOPMENT, AwsRegion.US_WEST_2),
    PDEV_APSE2("pdev-apse2", Environment.DEVELOPMENT, AwsRegion.AP_SOUTHEAST_2),
    PDEV_WEST2("pdev-west2", Environment.DEVELOPMENT, AwsRegion.US_WEST_2),

    // Local
    LOCAL("local", Environment.LOCAL, AwsRegion.US_EAST_1),
    ;

    @JsonValue
    fun toValue(): String = value

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun fromValue(value: String): MicrosEnvironment =
            MicrosEnvironment.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown Micros environment: $value")
    }
}

enum class AwsRegion(
    val value: String,
) {
    US_EAST_1("us-east-1"), // USA (N. Virginia)
    US_WEST_1("us-west-1"), // USA (North California)
    US_WEST_2("us-west-2"), // USA (Oregon)
    CA_CENTRAL_1("ca-central-1"), // Canada (Central)
    EU_CENTRAL_1("eu-central-1"), // Germany (Frankfurt)
    EU_CENTRAL_2("eu-central-2"), // Switzerland (Zurich)
    EU_WEST_1("eu-west-1"), // Ireland (Dublin)
    EU_WEST_2("eu-west-2"), // United Kingdom (London)
    AP_NORTHEAST_1("ap-northeast-1"), // Japan (Tokyo)
    AP_NORTHEAST_2("ap-northeast-2"), // South Korea (Seoul)
    AP_SOUTHEAST_1("ap-southeast-1"), // Singapore
    AP_SOUTHEAST_2("ap-southeast-2"), // Australia (Sydney)
    AP_SOUTH_1("ap-south-1"), // India (Mumbai)
    ;

    @JsonValue
    fun toValue(): String = value

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun fromValue(value: String): AwsRegion =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown AWS region: $value")
    }
}
