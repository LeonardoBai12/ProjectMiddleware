package extensions

import com.android.build.api.dsl.CommonExtension
import provider.libs
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReportBase
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk =  libs.findVersion("compileSdk").get().toString().toInt()

        defaultConfig {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()
        }

        compileOptions {
            sourceCompatibility = COMPILE_VERSION
            targetCompatibility = COMPILE_VERSION
        }
    }

    configureKotlin()
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = COMPILE_VERSION
        targetCompatibility = COMPILE_VERSION
    }

    configureKotlin()
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = COMPILE_VERSION.toString()
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )
        }
    }
}
internal fun JacocoCoverageVerification.setupCoverageVerification(
    minimumCoverage: Int = 80
) {
    violationRules {
        rule {
            limit {
                minimum = minimumCoverage.toBigDecimal()
            }
        }
    }
}

internal fun Project.setupJacoco() {
    pluginManager.apply("jacoco")

    extensions.configure<JacocoPluginExtension> {
        toolVersion = libs.findVersion("jacoco").get().preferredVersion
    }
    tasks.withType<Test> {
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}

internal fun Project.setJacocoJvmDirectories(
    jacocoReport: JacocoReportBase
) {
    jacocoReport.apply {
        executionData.from(fileTree("${layout.buildDirectory}/jacoco/test.exec"))
        setJacocoDirectories(this)
    }
}

internal fun Project.setJacocoAndroidDirectories(
    jacocoReport: JacocoReportBase
) {
    jacocoReport.apply {
        executionData.from(fileTree("${layout.buildDirectory}/jacoco/testDebugUnitTest.exec"))
        setJacocoDirectories(this)
    }
}

internal fun Project.setJacocoDirectories(
    jacocoReport: JacocoReportBase
) {
    val customClassDirectories = files(layout.projectDirectory).asFileTree.matching {
        exclude(
            "**/build/generated/**",
            "**/test/**",
            "**/Application.kt",
            "**/*Configuration.kt",
            "**/*Kt$*",
            "**/internal/**",
            "**/R.class",
            "**/BuildConfig.*",
            "**/session/**",
            "**/model/**",
            "**/plugins/**",
            "**/di/**",
            "**/security/**",
        )
    }

    jacocoReport.apply {
        classDirectories.setFrom(customClassDirectories)
    }
}

fun Project.isJvm(): Boolean {
    return this.plugins.hasPlugin("io.lb.jvm.library")
}
