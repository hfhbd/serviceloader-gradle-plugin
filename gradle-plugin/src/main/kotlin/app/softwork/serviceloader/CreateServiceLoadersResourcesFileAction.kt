package app.softwork.serviceloader

import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.workers.*
import java.io.*
import java.lang.reflect.*
import java.net.*

internal abstract class CreateServiceLoadersResourcesFileAction :
    WorkAction<CreateServiceLoadersResourcesFileAction.Parameters> {
    interface Parameters : WorkParameters {
        val serviceLoader: Property<String>
        val implementationClasses: ListProperty<String>
        val resourcesDir: DirectoryProperty
        val classes: ConfigurableFileCollection
    }

    override fun execute() {
        parameters.resourcesDir.asFile.get().mkdirs()

        val classLoader = CreateServiceLoadersResourcesFileAction::class.java.classLoader
        val provider = classLoader.loadClass(parameters.serviceLoader.get())

        val urls = parameters.classes.flatMap {
            it.walk()
        }.map { it.toURI().toURL() }
        val classes = URLClassLoader.newInstance(urls.toTypedArray(), classLoader)

        val serviceLoaderFile = File(parameters.resourcesDir.asFile.get(), provider.name)
        if (!serviceLoaderFile.exists()) {
            serviceLoaderFile.createNewFile()
        }
        serviceLoaderFile.writeText(parameters.implementationClasses.get().map { classes.loadClass(it) }
            .joinToString("\n", postfix = "\n") {
                // https://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html

                require(provider.isAssignableFrom(it)) {
                    "Class $it does not implement or extend from $provider."
                }
                
                require(it.constructors.any { it.parameterCount == 0 }) {
                    "Class $it does not have a non-zero arg public constructor."
                }
                require(!Modifier.isAbstract(it.modifiers)) {
                    "Class $it is abstract."
                }
                it.name
            })
    }
}
