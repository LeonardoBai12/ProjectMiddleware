plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.ktor.http)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
}