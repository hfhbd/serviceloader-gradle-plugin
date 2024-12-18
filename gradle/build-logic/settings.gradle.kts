rootProject.name = "build-logic"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    versionCatalogs.register("libs") {
        from(files("../libs.versions.toml"))
    }
}
