import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial

class DokkaModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.dokka")

            tasks.withType<DokkaTaskPartial> {
                dokkaSourceSets.configureEach {
                    if (file("Packages.md").exists()) {
                        includes.from("Packages.md")
                    }
                    if (file("Module.md").exists()) {
                        includes.from("Module.md")
                    }
                    documentedVisibilities.set(
                        setOf(
                            DokkaConfiguration.Visibility.PUBLIC
                        )
                    )
                }
            }
        }
    }
}
