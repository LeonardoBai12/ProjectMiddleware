import extensions.implementation
import extensions.testImplementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":common:shared"))
    implementation(project(":common:data"))
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.jbcrypt)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.junit.jupiter.api)
}
