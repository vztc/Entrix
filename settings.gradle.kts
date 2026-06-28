pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Entrix"

include(":app")

include(":core:model")
include(":core:common")
include(":core:designsystem")
include(":core:database")
include(":core:datastore")
include(":core:location")
include(":core:navigation")

include(":domain")
include(":data")

include(":feature:dashboard")
include(":feature:attendance")
include(":feature:office")
include(":feature:settings")
