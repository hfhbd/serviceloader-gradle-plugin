package app.softwork.serviceloader

import org.gradle.api.*
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.*

public class ServiceLoaderGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val serviceLoadersExtension = project.objects.domainObjectContainer(ServiceLoader::class.java)
        project.extensions.add("serviceLoaders", serviceLoadersExtension)

        val createServiceLoadersResourcesFile by project.tasks.registering(CreateServiceLoadersResourcesFileTask::class) {
            serviceLoaders.convention(project.provider<Map<String, List<String>>> {
                val map = mutableMapOf<String, List<String>>()
                for (serviceLoader in serviceLoadersExtension) {
                    map[serviceLoader.name] = serviceLoader.implementationClasses.get()
                }
                map
            })
        }

        project.tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) {
            dependsOn(createServiceLoadersResourcesFile)
        }
    }
}
