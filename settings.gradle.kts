pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ProjectMiddleware"

include(":common:shared")
include(":common:data")
include(":impl:ktor-client")
include(":impl:ktor-server")
include(":impl:mongo-database")
include(":middleware:core")
include(":middleware:data")
include(":middleware:domain")
include(":middleware:mapper")
