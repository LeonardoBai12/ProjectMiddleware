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
    implementation(libs.mongo.db.kt)
    implementation(libs.mongo.db.bson)
    implementation(libs.slf4.simple)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)
    testImplementation(libs.junit.jupiter.api)
}
