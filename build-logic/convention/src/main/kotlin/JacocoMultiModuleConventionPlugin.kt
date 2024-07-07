import extensions.isJvm
import extensions.setJacocoAndroidDirectories
import extensions.setJacocoJvmDirectories
import extensions.setupCoverageVerification
import extensions.setupJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

class JacocoMultiModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val module = project.name.capitalized()
            val report = "jacoco${module}CoverageReport"
            val verification = "jacoco${module}CoverageVerification"

            setupJacoco()

            tasks.register(report, JacocoReport::class.java) {
                group = "verification"
                description = "Generates $module coverage report."

                subprojects.forEach {
                    if (it.isJvm()) {
                        it.setJacocoJvmDirectories(this)
                        dependsOn("test")
                    } else {
                        it.setJacocoAndroidDirectories(this)
                        dependsOn("testDebugUnitTest")
                    }
                }
            }

            tasks.register(verification, JacocoCoverageVerification::class.java) {
                group = "verification"
                description = "Validates $module coverage."

                setupCoverageVerification()
                dependsOn(report)

                subprojects.forEach {
                    if (it.isJvm()) {
                        it.setJacocoJvmDirectories(this)
                    } else {
                        it.setJacocoAndroidDirectories(this)
                    }
                }
            }
        }
    }
}
