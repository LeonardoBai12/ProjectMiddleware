import com.android.build.api.dsl.ApplicationExtension
import extensions.androidTestImplementation
import extensions.configureKotlinAndroid
import extensions.debugImplementation
import extensions.implementation
import extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import provider.libs

class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.findVersion("targetSdk")
                    .get()
                    .toString()
                    .toInt()
            }

            dependencies {
                with(libs) {
                    implementation(findLibrary("androidx-core-ktx").get())
                    implementation(findLibrary("androidx-lifecycle-runtime-ktx").get())
                    implementation(findLibrary("androidx-activity-compose").get())
                    implementation(platform(findLibrary("androidx-compose-bom").get()))
                    implementation(findLibrary("androidx-ui").get())
                    implementation(findLibrary("androidx-ui-graphics").get())
                    implementation(findLibrary("androidx-ui-tooling-preview").get())
                    implementation(findLibrary("androidx-material3").get())
                    testImplementation(findLibrary("mockk").get())
                    testImplementation(findLibrary("junit-jupiter-api").get())
                    testImplementation(findLibrary("junit-jupiter-params").get())
                    testImplementation(findLibrary("assertk").get())
                    testImplementation(findLibrary("kotlinx-coroutines-test").get())
                    testImplementation(findLibrary("kotlin-test-junit").get())
                    androidTestImplementation(findLibrary("androidx-espresso-core").get())
                    debugImplementation(findLibrary("androidx-ui-tooling").get())
                }
            }
        }
    }
}
