plugins {
    `kotlin-dsl`
    setup
}

val kotlinFiles by configurations.creating

dependencies {
    compileOnly(kotlin("gradle-plugin"))

    testImplementation(kotlin("test"))
    kotlinFiles(kotlin("gradle-plugin"))
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
    environment("kotlinFiles", kotlinFiles.joinToString(":"))
}
