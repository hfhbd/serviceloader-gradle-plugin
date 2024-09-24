import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    id("myPublish")
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(8)
        explicitApi()
        compilerOptions {
            allWarningsAsErrors.set(true)
        }
    }
}

pluginManager.withPlugin("org.gradle.java") {
    extensions.configure<JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()
    }
}
