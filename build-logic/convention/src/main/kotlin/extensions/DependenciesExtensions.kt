package extensions

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

val COMPILE_VERSION = JavaVersion.VERSION_17

fun DependencyHandlerScope.implementation(name: Provider<MinimalExternalModuleDependency>) {
    add("implementation", name)
}

fun DependencyHandlerScope.testImplementation(name: Provider<MinimalExternalModuleDependency>) {
    add("testImplementation", name)
}

fun DependencyHandlerScope.androidTestImplementation(name: Provider<MinimalExternalModuleDependency>) {
    add("androidTestImplementation", name)
}

fun DependencyHandlerScope.debugImplementation(name: Provider<MinimalExternalModuleDependency>) {
    add("debugImplementation", name)
}
