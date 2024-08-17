import extensions.implementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(project(":server"))
}