import app.softwork.serviceloader.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

private val serviceLoadersExtension = objects.domainObjectContainer(ServiceLoader::class.java)
extensions.add("serviceLoaders", serviceLoadersExtension)

private val createServiceLoadersResourcesFile by tasks.registering(CreateServiceLoadersResourcesFileTask::class) {
    serviceLoaders.convention(provider<Map<String, List<String>>> {
        val map = mutableMapOf<String, List<String>>()
        for (serviceLoader in serviceLoadersExtension) {
            map[serviceLoader.name] = serviceLoader.implementationClasses.get()
        }
        map
    })
}

plugins.withId("org.gradle.java") {
    extensions.getByName<JavaPluginExtension>("java").sourceSets.named("main") {
        resources.srcDir(createServiceLoadersResourcesFile)
    }
    createServiceLoadersResourcesFile {
        classpath.from(project.configurations.named(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME))
        classes.from(project.tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME))
    }
}

plugins.withId("org.jetbrains.kotlin.jvm") {
    val kotlin = extensions.getByName<KotlinJvmProjectExtension>("kotlin")
    kotlin.sourceSets.named("main") {
        resources.srcDir(createServiceLoadersResourcesFile)
    }
    createServiceLoadersResourcesFile {
        val jvmTarget = kotlin.target
        val mainComplication = jvmTarget.compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME)

        val compileTaskProvider = mainComplication.flatMap { it.compileTaskProvider }.map { it as KotlinJvmCompile }
        val classesPath = compileTaskProvider.map {
            it.libraries
        }
        classpath.from(classesPath)
        val klasses = compileTaskProvider.flatMap { 
            it.destinationDirectory
        }
        classes.from(klasses)
    }
}

plugins.withId("com.google.devtools.ksp") {
    val kspPluginDep = dependencies.create("app.softwork.serviceloader:ksp-plugin:$VERSION")
    val kspAnnotationDep = dependencies.create("app.softwork.serviceloader:ksp-annotation:$VERSION")

    plugins.withId("org.jetbrains.kotlin.jvm") {
        val kotlin = extensions.getByName<KotlinJvmProjectExtension>("kotlin")
        val jvmTarget = kotlin.target
        val mainComplication = jvmTarget.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME)

        dependencies.add(mainComplication.compileOnlyConfigurationName, kspAnnotationDep)
        dependencies.add("ksp", kspPluginDep)
    }
}
