plugins {
    `kotlin-dsl`
    setup
}

val kotlinFiles by configurations.creating

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")

    testImplementation(kotlin("test"))
    kotlinFiles("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
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
