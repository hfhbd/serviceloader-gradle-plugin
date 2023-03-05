pluginManagement {
    includeBuild("gradle/build-logic")
    repositories { 
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins { 
    id("myRepos")
}

rootProject.name = "serviceloader-gradle-plugin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(":gradle-plugin")

include(":ksp-annotation")
include(":ksp-plugin")
