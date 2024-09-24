package app.softwork.serviceloader

import org.gradle.testkit.runner.*
import java.io.*
import java.nio.file.*
import kotlin.io.path.*
import kotlin.test.*

class KspTesting {
    @Test
    fun kotlin() {
        val temp = Files.createTempDirectory("gradle")
        val tmp = temp.toFile()
        File(tmp, "build.gradle.kts").apply {
            createNewFile()
        }.writeText(
            //language=kotlin
            """
            |plugins {
            |  id("app.softwork.serviceloader")
            |  kotlin("jvm")
            |  id("com.google.devtools.ksp")
            |}
            |
            |repositories {
            |  mavenCentral()
            |}
            |
            |kotlin.jvmToolchain(8)
            |
        """.trimMargin()
        )
        val projectDir = System.getenv("projectDir")
        File(tmp, "settings.gradle.kts").apply { 
            createNewFile()
        }.writeText("""
            |includeBuild("$projectDir")
        """.trimMargin())
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
        assertEquals(TaskOutcome.SUCCESS, build.task(":kspKotlin")?.outcome, build.tasks.joinToString(prefix = temp.toString()))
        assertEquals(
            setOf("Foo"),
            (temp / "build/generated/ksp/main/resources/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet()
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
            |  id("app.softwork.serviceloader")
            |  kotlin("multiplatform")
            |  id("com.google.devtools.ksp")
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
            |  jvm("foo") {
            |    attributes {
            |      // https://youtrack.jetbrains.com/issue/KT-55751
            |      val KT_55751 = Attribute.of("KT_55751", String::class.java)
            |      attribute(KT_55751, "foo")
            |    } 
            |  }
            |  linuxX64()
            |}
            |
        """.trimMargin()
        )
        val projectDir = System.getenv("projectDir")
        File(tmp, "settings.gradle.kts").apply {
            createNewFile()
        }.writeText("""
            |includeBuild("$projectDir")
        """.trimMargin())
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
        assertEquals(TaskOutcome.SUCCESS, build.task(":kspKotlinJvm")?.outcome, build.tasks.joinToString())
        assertEquals(
            setOf("Foo", "CommonFoo"),
            (temp / "build/generated/ksp/jvm/jvmMain/resources/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet(),
            temp.toUri().toString()
        )
        assertEquals(
            setOf("CommonFoo"),
            (temp / "build/generated/ksp/foo/fooMain/resources/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet(),
            temp.toUri().toString()
        )
    }

    @Ignore
    @Test
    fun java() {
        val temp = Files.createTempDirectory("gradle")
        val tmp = temp.toFile()
        File(tmp, "build.gradle.kts").apply {
            createNewFile()
        }.writeText(
            //language=kotlin
            """
            |plugins {
            |  id("app.softwork.serviceloader")
            |  kotlin("jvm")
            |  id("com.google.devtools.ksp")
            |}
            |
            |repositories {
            |  mavenCentral()
            |}
            |
            |kotlin.jvmToolchain(8)
            |
        """.trimMargin()
        )
        val projectDir = System.getenv("projectDir")
        File(tmp, "settings.gradle.kts").apply {
            createNewFile()
        }.writeText("""
            |includeBuild("$projectDir")
        """.trimMargin())
        val java = File(tmp, "src/main/java").apply {
            mkdirs()
        }
        File(java, "Foo.java").apply {
            createNewFile()
        }.writeText(
            //language=Java
            """
            |import app.softwork.serviceloader.ServiceLoader;
            |
            |public interface Foo { }
        """.trimMargin()
        )

        File(java, "FooImpl.java").apply {
            createNewFile()
        }.writeText(
            //language=Java
            """
            |import app.softwork.serviceloader.ServiceLoader;
            |
            |@ServiceLoader(forClass = Foo.class)
            |public class FooImpl implements Foo {}
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
        assertEquals(TaskOutcome.SUCCESS, build.task(":kspKotlin")?.outcome, build.tasks.joinToString(prefix = temp.toString()))
        assertEquals(
            setOf("Foo"),
            (temp / "build/generated/ksp/main/resources/META-INF/services").toFile().listFiles()
                ?.map { it.name }?.toSet()
        )
    }
}
