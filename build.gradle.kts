plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka) apply true
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor.plugin)
    alias(libs.plugins.serialization)
    id("com.gradleup.shadow") version "8.3.1"
    id("io.lb.dokka") apply true
    id("io.lb.jacoco.multi-module")
}

application {
    mainClass.set("io.lb.middleware.core.MiddlewareApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(project(":common:shared"))
    implementation(project(":common:data"))
    implementation(project(":middleware:domain"))
    implementation(project(":middleware:data"))
    implementation(project(":middleware:mapper"))
    implementation(project(":impl:ktor-client"))
    implementation(project(":impl:ktor-server"))
    implementation(project(":impl:mongo-database"))
}