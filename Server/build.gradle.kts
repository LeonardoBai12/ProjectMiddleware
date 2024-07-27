plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":data"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.koin.ktor)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.tests)
}
