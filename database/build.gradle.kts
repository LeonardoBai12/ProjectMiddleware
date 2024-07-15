import extensions.implementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":data"))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.2")
    implementation("org.mongodb:bson-kotlin:5.1.2")
    implementation("org.slf4j:slf4j-simple:1.7.9")
    implementation(libs.kotlinx.serialization.json)
}
