plugins {
    `kotlin-dsl`
    id("setup")
}

val pluginFiles by configurations.creating

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)

    testImplementation(kotlin("test"))
    pluginFiles(libs.kotlin.gradlePlugin)
    pluginFiles(libs.ksp.gradlePlugin)
}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}

val version by tasks.registering(VersionTask::class)

kotlin.sourceSets.main {
    kotlin.srcDir(version)
}

gradlePlugin.plugins.configureEach { 
    displayName = "A Gradle plugin to generate and validate service loaders"
    description = "A Gradle plugin to generate and validate service loaders"
}

tasks.test {
    environment("pluginFiles", pluginFiles.joinToString(":"))
    environment("projectDir", project.rootDir.toString())
}
