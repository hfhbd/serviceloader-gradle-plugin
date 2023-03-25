import app.softwork.serviceloader.CreateServiceLoadersResourcesFileTask
import app.softwork.serviceloader.ServiceLoader
import app.softwork.serviceloader.VERSION
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

private val CreateServiceLoadersResourcesFileTaskName = "createServiceLoadersResourcesFile" 

private val serviceLoadersExtension = objects.domainObjectContainer(ServiceLoader::class.java)
extensions.add("serviceLoaders", serviceLoadersExtension)

private val configExtension: CreateServiceLoadersResourcesFileTask.() -> Unit = {
    serviceLoaders.convention(provider<Map<String, List<String>>> {
        val map = mutableMapOf<String, List<String>>()
        for (serviceLoader in serviceLoadersExtension) {
            map[serviceLoader.name] = serviceLoader.implementationClasses.get()
        }
        map
    })
}

pluginManager.withPlugin("org.gradle.java") {
    val serviceLoader = tasks.register(
        CreateServiceLoadersResourcesFileTaskName,
        CreateServiceLoadersResourcesFileTask::class,
        configExtension
    )
    extensions.getByName<JavaPluginExtension>("java").sourceSets.named("main") {
        resources.srcDir(serviceLoader)
    }
    serviceLoader {
        classpath.from(project.configurations.named(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME))
        classes.from(project.tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME))
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    val kotlin = extensions.getByName<KotlinJvmProjectExtension>("kotlin")
    val serviceLoader = if (CreateServiceLoadersResourcesFileTaskName in tasks.names) {
        tasks.named<CreateServiceLoadersResourcesFileTask>(CreateServiceLoadersResourcesFileTaskName)
    } else {
        tasks.register(
            CreateServiceLoadersResourcesFileTaskName,
            CreateServiceLoadersResourcesFileTask::class,
            configExtension
        )
    }

    kotlin.sourceSets.named("main") {
        resources.srcDir(serviceLoader)
    }
    serviceLoader {
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

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    val kotlin = extensions.getByName<KotlinMultiplatformExtension>("kotlin")
    val jvmTargets = kotlin.targets.withType(KotlinJvmTarget::class)
    jvmTargets.configureEach {
        val jvmTarget = this
        val serviceLoader = tasks.register(
            name = CreateServiceLoadersResourcesFileTaskName + jvmTarget.name.replaceFirstChar { it.uppercaseChar() },
            type = CreateServiceLoadersResourcesFileTask::class,
            configurationAction = configExtension
        )

        compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME) {
            val mainComplication = this
            kotlinSourceSets.forAll {
                it.resources.srcDir(serviceLoader)
            }

            serviceLoader {
                val compileTaskProvider =
                    mainComplication.compileTaskProvider.map { it as KotlinJvmCompile }
                val classesPath = compileTaskProvider.map {
                    it.libraries
                }
                classpath.from(classesPath)
                val klasses = compileTaskProvider.flatMap {
                    it.destinationDirectory
                }
                classes.from(klasses)
                resourcesDir.convention(project.layout.buildDirectory.dir("generated/${jvmTarget.name}/resources/serviceloader"))
            }
        }
    }
}

pluginManager.withPlugin("com.google.devtools.ksp") {
    val kspPluginDep = dependencies.create("app.softwork.serviceloader:ksp-plugin:$VERSION")
    val kspAnnotationDep = dependencies.create("app.softwork.serviceloader:ksp-annotation:$VERSION")

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        val kotlin = extensions.getByName<KotlinJvmProjectExtension>("kotlin")
        val jvmTarget = kotlin.target
        val mainComplication = jvmTarget.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME)

        dependencies.add(mainComplication.compileOnlyConfigurationName, kspAnnotationDep)
        dependencies.add("ksp", kspPluginDep)
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        val kotlin = extensions.getByName<KotlinMultiplatformExtension>("kotlin")
        val jvmTargets = kotlin.targets.withType(KotlinJvmTarget::class)
        jvmTargets.names.forEach { name ->
            val kspName = "ksp" + name.replaceFirstChar { it.uppercaseChar() }
            dependencies.add(kspName, kspPluginDep)
        }

        kotlin.sourceSets.named("commonMain") {
            dependencies.add(compileOnlyConfigurationName, kspAnnotationDep)
        }
    }
}
