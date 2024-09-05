import extensions.implementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":common:shared"))
    implementation(project(":common:data"))
    implementation(project(":middleware:domain"))
    implementation(project(":middleware:data"))
    implementation(project(":middleware:mapper"))
    implementation(project(":impl:ktor-client"))
    implementation(project(":impl:ktor-server"))
    implementation(project(":impl:mongo-database"))

    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.koin.logger.slf4j)
}