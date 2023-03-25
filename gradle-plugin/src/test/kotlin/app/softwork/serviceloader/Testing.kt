package app.softwork.serviceloader

import org.gradle.testkit.runner.*
import java.io.*
import java.nio.file.*
import kotlin.io.path.*
import kotlin.test.*

class Testing {
    @Test
    fun kotlin() {
        val temp = Files.createTempDirectory("gradle")
        val tmp = temp.toFile()
        File(tmp, "build.gradle.kts").apply {
            createNewFile()
        }.writeText(
            """
            |plugins {
            |  id("app.softwork.serviceloader")
            |  kotlin("jvm") version "1.8.10"
            |}
            |
            |repositories {
            |  mavenCentral()
            |}
            |
            |serviceLoaders.register("Foo") {
            |  implementationClasses.add("FooImpl")
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
            |interface Foo
            |
            |class FooImpl : Foo
        """.trimMargin()
        )

        val build = GradleRunner.create()
            .withPluginClasspath()
            .apply {
                val kgp = System.getenv("kotlinFiles")?.split(":")?.map { File(it) } ?: emptyList()
                withPluginClasspath(pluginClasspath + kgp)
            }
            .withProjectDir(tmp)
            .withArguments(":assemble", "--stacktrace", "--configuration-cache")
            .build()

        assertEquals(TaskOutcome.SUCCESS, build.task(":assemble")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, build.task(":createServiceLoadersResourcesFile")?.outcome)
        assertEquals(
            listOf("Foo"),
            (temp / "build/generated/resources/serviceloader/META-INF/services").toFile().listFiles()
                ?.map { it.name }
        )
    }

    @Test
    fun kotlinMpp() {
        val temp = Files.createTempDirectory("gradle")
        val tmp = temp.toFile()
        File(tmp, "build.gradle.kts").apply {
            createNewFile()
        }.writeText(
            """
            |plugins {
            |  id("app.softwork.serviceloader")
            |  kotlin("multiplatform") version "1.8.10"
            |}
            |
            |repositories {
            |  mavenCentral()
            |}
            |
            |kotlin {
            |  jvm()
            |}
            |
            |serviceLoaders.register("Foo") {
            |  implementationClasses.add("FooImpl")
            |}
            |
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
            |interface Foo
            |
            |class FooImpl : Foo
        """.trimMargin()
        )

        val build = GradleRunner.create()
            .withPluginClasspath()
            .apply {
                val kgp = System.getenv("kotlinFiles")?.split(":")?.map { File(it) } ?: emptyList()
                withPluginClasspath(pluginClasspath + kgp)
            }
            .withProjectDir(tmp)
            .withArguments(":assemble", "--stacktrace", "--configuration-cache")
            .build()

        assertEquals(TaskOutcome.SUCCESS, build.task(":assemble")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, build.task(":createServiceLoadersResourcesFileJvm")?.outcome)
        println(tmp)
        assertEquals(
            listOf("Foo"),
            (temp / "build/generated/jvm/resources/serviceloader/META-INF/services").toFile().listFiles()
                ?.map { it.name }
        )
    }

    @Test
    fun java() {
        val temp = Files.createTempDirectory("gradle")
        val tmp = temp.toFile()
        File(tmp, "build.gradle.kts").apply {
            createNewFile()
        }.writeText(
            """
            |plugins {
            |  id("app.softwork.serviceloader")
            |  java
            |}
            |
            |serviceLoaders.register("Foo") {
            |  implementationClasses.add("FooImpl")
            |}
            |
        """.trimMargin()
        )
        val java = File(tmp, "src/main/java").apply {
            mkdirs()
        }
        File(java, "Foo.java").apply {
            createNewFile()
        }.writeText(
            //language=java
            """
            |public interface Foo { }
        """.trimMargin()
        )

        File(java, "FooImpl.java").apply {
            createNewFile()
        }.writeText(
            //language=java
            """
            |public class FooImpl implements Foo { }
        """.trimMargin()
        )

        val build = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(tmp)
            .withArguments("assemble", "--configuration-cache")
            .build()

        assertEquals(TaskOutcome.SUCCESS, build.task(":assemble")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, build.task(":createServiceLoadersResourcesFile")?.outcome)
        assertEquals(
            listOf("Foo"),
            (temp / "build/generated/resources/serviceloader/META-INF/services").toFile().listFiles()
                ?.map { it.name }
        )
    }
}
