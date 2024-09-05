import extensions.implementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":common:shared"))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
}
