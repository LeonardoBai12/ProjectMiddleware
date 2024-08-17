import extensions.implementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":data"))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.koin.ktor)
    testImplementation(libs.ktor.client.mock)
}
