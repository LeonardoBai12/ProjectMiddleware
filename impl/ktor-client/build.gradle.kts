import extensions.implementation
import extensions.testImplementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":common:shared"))
    implementation(project(":common:data"))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.koin.ktor)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.junit.jupiter.api)
}
