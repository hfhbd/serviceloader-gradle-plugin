rootProject.name = "ksp-sample"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("..") {
    dependencySubstitution {
        substitute(module("app.softwork.serviceloader:ksp-plugin")).using(project(":ksp-plugin"))
        substitute(module("app.softwork.serviceloader:ksp-annotation")).using(project(":ksp-annotation"))
    }
}
