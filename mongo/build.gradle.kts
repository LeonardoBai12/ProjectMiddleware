import extensions.implementation

plugins {
    id("io.lb.jvm.library")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.mongo.db.kt)
    implementation(libs.mongo.db.bson)
    implementation(libs.slf4.simple)
    implementation(libs.kotlinx.serialization.json)
}
