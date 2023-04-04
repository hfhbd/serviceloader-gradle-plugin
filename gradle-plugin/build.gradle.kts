plugins {
    `kotlin-dsl`
    setup
}

val pluginFiles by configurations.creating
val kspPlugin by configurations.creating
val kspAnnotation by configurations.creating

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")

    testImplementation(kotlin("test"))
    pluginFiles("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20-RC2")
    pluginFiles("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.8.20-1.0.10")
    kspPlugin(projects.kspPlugin)
    kspAnnotation(projects.kspAnnotation)
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
    environment("kspPlugin", kspPlugin.joinToString(":"))
    environment("kspAnnotation", kspAnnotation.joinToString(":"))
    environment("projectDir", project.rootDir.toString())
}
