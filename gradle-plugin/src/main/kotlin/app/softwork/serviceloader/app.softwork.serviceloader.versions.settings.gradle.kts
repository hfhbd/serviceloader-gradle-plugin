dependencyResolutionManagement.versionCatalogs.register("serviceloader") {
    val version = version("serviceloader", app.softwork.serviceloader.VERSION)

    plugin("serviceloader", "app.softwork.serviceloader").versionRef(version)

    library("ksp-annotation", "app.softwork.serviceloader", "ksp-annotation").versionRef(version)
    library("ksp-plugin", "app.softwork.serviceloader", "ksp-plugin").versionRef(version)
}
