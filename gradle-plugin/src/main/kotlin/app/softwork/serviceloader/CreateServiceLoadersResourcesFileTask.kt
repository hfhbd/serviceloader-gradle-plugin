package app.softwork.serviceloader

import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.workers.*
import javax.inject.*

@CacheableTask
public abstract class CreateServiceLoadersResourcesFileTask : DefaultTask() {
    @get:Input
    public abstract val serviceLoaders: MapProperty<String, List<String>>

    @get:InputFiles
    @get:CompileClasspath
    public abstract val classpath: ConfigurableFileCollection

    @get:InputFiles
    @get:CompileClasspath
    public abstract val classes: ConfigurableFileCollection

    @get:OutputDirectory
    public abstract val resourcesDir: DirectoryProperty

    init {
        resourcesDir.convention(project.layout.buildDirectory.dir("generated/resources/serviceloader"))
    }

    @get:Inject
    internal abstract val worker: WorkerExecutor

    @TaskAction
    internal fun create() {
        val workQueue = worker.classLoaderIsolation {
            classpath.from(this@CreateServiceLoadersResourcesFileTask.classpath, classes)
        }
        for ((serviceLoader, classes) in serviceLoaders.get()) {
            workQueue.submit(CreateServiceLoadersResourcesFileAction::class.java) {
                this.serviceLoader.set(serviceLoader)
                this.implementationClasses.set(classes)
                this.resourcesDir.set(this@CreateServiceLoadersResourcesFileTask.resourcesDir.dir("META-INF/services"))
                this.classes.setFrom(this@CreateServiceLoadersResourcesFileTask.classes)
            }
        }
    }
}
