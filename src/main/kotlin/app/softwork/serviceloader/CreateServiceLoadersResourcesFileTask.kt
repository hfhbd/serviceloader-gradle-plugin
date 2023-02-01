package app.softwork.serviceloader

import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.workers.*
import javax.inject.*

@CacheableTask
public abstract class CreateServiceLoadersResourcesFileTask : DefaultTask() {
    @get:Input
    public abstract val serviceLoaders: MapProperty<String, List<String>>

    @get:OutputDirectory
    public abstract val resourcesDir: DirectoryProperty
    
    @get:InputFiles
    @get:CompileClasspath
    public abstract val classpath: ConfigurableFileCollection
    
    @get:InputFiles
    @get:CompileClasspath
    public abstract val classes: ConfigurableFileCollection

    init {
        resourcesDir.convention(project.layout.buildDirectory.dir("resources/main/META-INF/services"))

        project.plugins.withId("org.gradle.java") {
            classpath.from(project.configurations.named(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME))
            classes.from(project.tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME))
        }
        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            classpath.from(project.configurations.named(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME))
            classes.from(project.tasks.named("compileKotlin"))
        }
    }

    @get:Inject
    internal abstract val worker: WorkerExecutor

    @TaskAction
    internal fun create() {
        val workQueue = worker.classLoaderIsolation { 
            classpath.from(this@CreateServiceLoadersResourcesFileTask.classpath)
        }
        for ((serviceLoader, classes) in serviceLoaders.get()) {
            workQueue.submit(CreateServiceLoadersResourcesFileAction::class.java) {
                this.serviceLoader.set(serviceLoader)
                this.implementationClasses.set(classes)
                this.resourcesDir.set(this@CreateServiceLoadersResourcesFileTask.resourcesDir)
                this.classes.setFrom(this@CreateServiceLoadersResourcesFileTask.classes)
            }
        }
    }
}
