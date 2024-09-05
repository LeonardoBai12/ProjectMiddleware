import extensions.implementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":common:shared"))
    implementation(project(":middleware:domain"))
    implementation(project(":impl:ktor-server"))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.koin.logger.slf4j)
}