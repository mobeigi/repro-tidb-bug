group = "io.atlassian.micros.studio"
version = "1.0-SNAPSHOT"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // BOM's
    implementation(platform(libs.kotlin.bom))
    implementation(platform(libs.spring.boot.dependencies))

    // Annotation processing
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Kotlin core
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    // Jackson for JSON processing
    implementation(libs.jackson.module.kotlin)

    // Spring Boot starters
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.jdbc)

    // MySQL driver
    implementation(libs.mysql.connector.j)

    // Caffeine caching library
    implementation(libs.caffeine)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)

    // Micrometer Tracing
    implementation(platform(libs.micrometer.tracing.bom))
    implementation(libs.micrometer.tracing)
    implementation(libs.micrometer.tracing.bridge)

    // Test dependencies
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.flyway.core)
    testImplementation(libs.flyway.mysql)
    testImplementation(libs.flyway.database.tidb)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(JavaVersion.VERSION_21.majorVersion.toInt())
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

tasks.test {
    useJUnitPlatform()
    enabled = false
}

val intTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"

    useJUnitPlatform()

    testClassesDirs =
        sourceSets.test
            .get()
            .output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    filter {
        includeTestsMatching("*IT")
        isFailOnNoMatchingTests = true
    }

    outputs.upToDateWhen { false }
}
