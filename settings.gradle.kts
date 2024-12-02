pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application") version "8.4.0" // Actualiza la versión de Gradle Plugin
        id("org.jetbrains.kotlin.android") version "1.8.0" // Actualiza la versión de Kotlin, si es necesario
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Speedrun_Compose"
include(":app")
 