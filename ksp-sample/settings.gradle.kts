pluginManagement {
    includeBuild("..")
}

rootProject.name = "ksp-sample"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

includeBuild("..") {
    dependencySubstitution {
        substitute(module("app.softwork.serviceloader:ksp-plugin")).using(project(":ksp-plugin"))
        substitute(module("app.softwork.serviceloader:ksp-annotation")).using(project(":ksp-annotation"))
    }
}

include(":lib")
include(":impl")

include(":mpp")
include(":mpp-plugin")
