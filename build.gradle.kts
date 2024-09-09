plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka) apply true
    alias(libs.plugins.ktor.plugin) apply true
    alias(libs.plugins.serialization)
    id("io.lb.dokka") apply true
    id("io.lb.jacoco.multi-module")
}

application {
    mainClass.set("io.lb.middleware.core.MiddlewareApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}
