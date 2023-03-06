plugins {
    `kotlin-dsl`
    setup
}

dependencies {
    implementation(kotlin("gradle-plugin"))

    testImplementation(kotlin("test"))
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
