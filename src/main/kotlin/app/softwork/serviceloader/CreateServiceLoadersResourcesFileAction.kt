package app.softwork.serviceloader

import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.workers.*
import java.io.*
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
        val classLoader = CreateServiceLoadersResourcesFileAction::class.java.classLoader
        classLoader.loadClass(parameters.serviceLoader.get())
        
        val serviceLoaderFile = File(parameters.resourcesDir.asFile.get(), parameters.serviceLoader.get())
        if (!serviceLoaderFile.exists()) {
            serviceLoaderFile.createNewFile()
        }
        
        val urls = parameters.classes.flatMap {
            it.walk()
        }.map { it.toURI().toURL() }
        val classes = URLClassLoader.newInstance(urls.toTypedArray(), classLoader)
        
        serviceLoaderFile.writeText(parameters.implementationClasses.get().joinToString("\n", postfix = "\n") {
            val klass = classes.loadClass(it)
            require(klass.constructors.any { it.parameterCount == 0 }) { 
                "Class $it has a non-zero arg public constructor." 
            }
            it
        })
    }
}
