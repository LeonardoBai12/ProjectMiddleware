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
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit.jupiter.api)
}
