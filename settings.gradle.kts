pluginManagement {
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

dependencyResolutionManagement {
    // Nous utilisons FAIL_ON_PROJECT_REPOS pour forcer toutes les repositories à être définies ici
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // JitPack pour imagepicker
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "DIA_PADCV"
include(":app")