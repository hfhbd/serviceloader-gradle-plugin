package app.softwork.serviceloader

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.listProperty
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.FilesSubpluginOption
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

public class ServiceLoaderPlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        super.apply(target)

        target.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val kotlin = target.extensions.getByType(KotlinMultiplatformExtension::class.java)
            kotlin.sourceSets.configureEach {
                dependencies {
                    implementation(runtimeDependency())
                }
            }
        }
        target.plugins.withId("org.jetbrains.kotlin.jvm") {
            val kotlin = target.extensions.getByType(KotlinJvmProjectExtension::class.java)
            kotlin.sourceSets.configureEach {
                dependencies {
                    implementation(runtimeDependency())
                }
            }
        }
    }

    private fun runtimeDependency() = "app.softwork.serviceloader:ksp-annotation:$VERSION"

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.platformType == KotlinPlatformType.jvm

    override fun getCompilerPluginId(): String = "app.softwork.serviceloader"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "app.softwork.serviceloader",
        artifactId = "kotlin-plugin",
        version = VERSION,
    )

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val outputDir =
            project.layout.buildDirectory.dir("generated/serviceloader/${kotlinCompilation.defaultSourceSet.name}/resources")
        val compileTaskProvider = kotlinCompilation.compileTaskProvider
        compileTaskProvider.configure {
            outputs.dir(outputDir)
        }
        val kotlinSourceSet = kotlinCompilation.defaultSourceSet
        kotlinSourceSet.resources.srcDir(outputDir)
        val processResourcesTaskName = if (kotlinSourceSet.name == "main") {
            "processResources"
        } else {
            if (project.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                kotlinSourceSet.name.removeSuffix("Main") + "ProcessResources"
            } else {
                "process" + kotlinSourceSet.name.replaceFirstChar { it.uppercaseChar() } + "Resources"
            }
        }
        kotlinCompilation.project.tasks.named(processResourcesTaskName) {
            dependsOn(compileTaskProvider)
        }

        val options = kotlinCompilation.project.objects.listProperty<SubpluginOption>()
        options.add(
            FilesSubpluginOption(
                key = "outputDir",
                files = listOf(outputDir.get().asFile),
            )
        )
        return options
    }
}
