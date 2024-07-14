import extensions.configureKotlinJvm
import extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import provider.libs

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("io.lb.jacoco.module")
                apply("io.lb.detekt")
                apply("io.lb.dokka")
            }

            extensions.configure<KotlinJvmProjectExtension> {
                configureKotlinJvm()
            }

            dependencies {
                with(libs) {
                    testImplementation(findLibrary("mockk").get())
                    testImplementation(findLibrary("junit-jupiter-api").get())
                    testImplementation(findLibrary("junit-jupiter-params").get())
                    testImplementation(findLibrary("assertk").get())
                    testImplementation(findLibrary("kotlinx-coroutines-test").get())
                    testImplementation(findLibrary("kotlin-test-junit").get())
                }
            }
        }
    }
}
