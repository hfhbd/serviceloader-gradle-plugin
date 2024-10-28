plugins {
    `kotlin-dsl`
    id("setup")
}

kotlin.jvmToolchain(21)

val pluginFiles by configurations.creating

dependencies {
    compileOnly(libs.plugins.ksp.toDep())
    runtimeOnly(libs.plugins.ksp.toDep())

    implementation(libs.plugins.kotlin.jvm.toDep())

    testImplementation(kotlin("test"))
    pluginFiles(libs.plugins.ksp.toDep())
}

fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}

val version by tasks.registering(VersionTask::class)

sourceSets.main {
    kotlin.srcDir(version)
}

gradlePlugin.plugins.configureEach {
    displayName = "A Gradle plugin to generate and validate service loaders"
    description = "A Gradle plugin to generate and validate service loaders"
}
gradlePlugin.plugins.register("serviceloader") {
    id = "app.softwork.serviceloader-compiler"
    implementationClass = "app.softwork.serviceloader.ServiceLoaderPlugin"
}

tasks.test {
    environment("pluginFiles", pluginFiles.joinToString(":"))
    environment("projectDir", project.rootDir.toString())
}
