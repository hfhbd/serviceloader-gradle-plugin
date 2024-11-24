package app.softwork.serviceloader

import org.gradle.testkit.runner.*
import java.io.*
import java.nio.file.*
import kotlin.io.path.*
import kotlin.test.*

class KotlinTesting {
    @Test
    fun kotlinJvm() {
        val temp = Files.createTempDirectory("gradle")
        val tmp = temp.toFile()
        File(tmp, "build.gradle.kts").apply {
            createNewFile()
        }.writeText(
            //language=kotlin
            """
            |plugins {
            |  kotlin("jvm")
            |  id("app.softwork.serviceloader-compiler")
            |}
            |
            |repositories {
            |  mavenCentral()
            |}
            |
            |kotlin.jvmToolchain(8)
            |
            |sourceSets.register("bar")
            |
            |java.withSourcesJar()
            |
        """.trimMargin()
        )
        val projectDir = System.getenv("projectDir")
        File(tmp, "settings.gradle.kts").apply {
            createNewFile()
        }.writeText(
            """
            |includeBuild("$projectDir")
            |
            |plugins {
            |  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
            |}
            |
        """.trimMargin()
        )
        val kotlin = File(tmp, "src/main/kotlin").apply {
            mkdirs()
        }
        File(kotlin, "Foo.kt").apply {
            createNewFile()
        }.writeText(
            //language=kotlin
            """
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Foo
            |
            |@ServiceLoader(Foo::class)
            |class FooImpl : Foo
        """.trimMargin()
        )

        val bar = File(tmp, "src/bar/kotlin").apply {
            mkdirs()
        }
        File(bar, "Bar.kt").apply {
            createNewFile()
        }.writeText(
            //language=kotlin
            """
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl : Bar
        """.trimMargin()
        )

        val build = GradleRunner.create()
            .withPluginClasspath()
            .apply {
                val pluginFiles = System.getenv("pluginFiles")?.split(":")?.map { File(it) } ?: emptyList()
                withPluginClasspath(pluginClasspath + pluginFiles)
            }
            .withProjectDir(tmp)
            .withArguments(":build", ":compileBarKotlin", "--stacktrace", "--configuration-cache")
            .build()

        assertEquals(TaskOutcome.SUCCESS, build.task(":assemble")?.outcome)
        assertEquals(
            setOf("Foo"),
            (temp / "build/generated/serviceloader/main/resources/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet(),
            temp.toUri().toString(),
        )
        assertEquals(
            setOf("Bar"),
            (temp / "build/generated/serviceloader/bar/resources/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet(),
            temp.toUri().toString(),
        )
        assertEquals(
            setOf("Foo"),
            (temp / "build/resources/main/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet(),
            temp.toUri().toString(),
        )
    }

    @Test
    fun kotlinMpp() {
        val temp = Files.createTempDirectory("gradle")
        val tmp = temp.toFile()
        File(tmp, "build.gradle.kts").apply {
            createNewFile()
        }.writeText(
            //language=kotlin
            """
            |plugins {
            |  kotlin("multiplatform")
            |  id("app.softwork.serviceloader-compiler")
            |}
            |
            |repositories {
            |  mavenCentral()
            |}
            |
            |kotlin {
            |  jvmToolchain(8)
            |
            |  jvm()
            |  linuxX64()
            |}
            |
        """.trimMargin()
        )
        val projectDir = System.getenv("projectDir")
        File(tmp, "settings.gradle.kts").apply {
            createNewFile()
        }.writeText(
            """
            |includeBuild("$projectDir")
            |
            |plugins {
            |  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
            |}
            
        """.trimMargin()
        )
        val kotlin = File(tmp, "src/jvmMain/kotlin").apply {
            mkdirs()
        }
        File(kotlin, "Foo.kt").apply {
            createNewFile()
        }.writeText(
            //language=kotlin
            """
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Foo
            |
            |@ServiceLoader(Foo::class)
            |class FooImpl : Foo
        """.trimMargin()
        )

        val common = File(tmp, "src/commonMain/kotlin").apply {
            mkdirs()
        }
        File(common, "Foo.kt").apply {
            createNewFile()
        }.writeText(
            //language=kotlin
            """
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface CommonFoo
            |
            |@ServiceLoader(CommonFoo::class)
            |class CommonFooImpl : CommonFoo
        """.trimMargin()
        )

        val build = GradleRunner.create()
            .withPluginClasspath()
            .apply {
                val pluginFiles = System.getenv("pluginFiles")?.split(":")?.map { File(it) } ?: emptyList()
                withPluginClasspath(pluginClasspath + pluginFiles)
            }
            .withProjectDir(tmp)
            .withArguments(":assemble", "--stacktrace", "--configuration-cache")
            .build()

        assertEquals(TaskOutcome.SUCCESS, build.task(":assemble")?.outcome)

        assertEquals(
            setOf("Foo", "CommonFoo"),
            (temp / "build/generated/serviceloader/jvmMain/resources/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet(),
            temp.toUri().toString(),
        )

        assertEquals(
            setOf("Foo", "CommonFoo"),
            (temp / "build/processedResources/jvm/main/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet(),
            temp.toUri().toString(),
        )
    }
}
