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
