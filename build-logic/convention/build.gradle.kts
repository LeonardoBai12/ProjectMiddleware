import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "io.lb.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("JvmLibraryConventionPlugin") {
            id = "io.lb.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("AndroidLibraryConventionPlugin") {
            id = "io.lb.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("AndroidAppConventionPlugin") {
            id = "io.lb.android.app"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("DetektConventionPlugin") {
            id = "io.lb.detekt"
            implementationClass = "DetektConventionPlugin"
        }
    }
}
