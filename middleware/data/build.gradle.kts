import extensions.implementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":common:shared"))
    implementation(project(":common:data"))
    implementation(project(":middleware:domain"))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.http)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
}
