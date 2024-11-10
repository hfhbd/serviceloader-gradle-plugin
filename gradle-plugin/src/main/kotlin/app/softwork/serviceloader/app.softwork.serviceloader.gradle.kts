import app.softwork.serviceloader.VERSION
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    id("com.google.devtools.ksp")
}

private val kspPluginDep = dependencies.create("app.softwork.serviceloader:ksp-plugin:$VERSION")
private val kspAnnotationDep = dependencies.create("app.softwork.serviceloader:runtime:$VERSION")

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    val sourceSets = extensions.getByName("sourceSets") as SourceSetContainer
    sourceSets.configureEach {
        dependencies.add(compileOnlyConfigurationName, kspAnnotationDep)
        if (name == MAIN_SOURCE_SET_NAME) {
            dependencies.add("ksp", kspPluginDep)
        } else {
            dependencies.add("ksp" + name.replaceFirstChar { it.uppercaseChar() }, kspPluginDep)
        }
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    val kotlin = extensions.getByName<KotlinMultiplatformExtension>("kotlin")
    val jvmTargets = kotlin.targets.withType(KotlinJvmTarget::class)
    jvmTargets.configureEach {
        val kspName = "ksp" + name.replaceFirstChar { it.uppercaseChar() }
        dependencies.add(kspName, kspPluginDep)
    }

    kotlin.sourceSets.named("commonMain") {
        dependencies.add(compileOnlyConfigurationName, kspAnnotationDep)
    }
}
