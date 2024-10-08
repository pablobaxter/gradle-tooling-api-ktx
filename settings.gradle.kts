pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()
        maven("https://repo.gradle.org/gradle/libs-releases")
    }
}

rootProject.name = "gradle-tooling-api-ktx"

include(":gradle-tooling-api-ktx")
