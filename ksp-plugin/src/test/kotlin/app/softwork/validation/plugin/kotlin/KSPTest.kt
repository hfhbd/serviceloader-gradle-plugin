package app.softwork.validation.plugin.kotlin

import com.google.devtools.ksp.KspExperimental
import com.tschuchort.compiletesting.*
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.*

@ExperimentalCompilerApi
@KspExperimental
class KSPTest {
    @Test
    fun simpleWorks() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl : Bar
            """.trimMargin(),
        )

        val result = jvmCompile(source)
        assertEquals(ExitCode.OK, result.exitCode, result.messages)
        val generatedFile = result.sourcesGeneratedBySymbolProcessor.single()
        assertEquals("foo.bar.Bar", generatedFile.name)
        assertEquals("foo.bar.BarImpl\n", generatedFile.readText())
    }

    @Test
    fun nestedWorks() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar {
            |  interface A
            |
            |  @ServiceLoader(A::class)
            |  class BarImpl : A
            |}
            """.trimMargin(),
        )
        val result = jvmCompile(source)
        assertEquals(ExitCode.OK, result.exitCode, result.messages)
        val generatedFile = result.sourcesGeneratedBySymbolProcessor.single()
        assertEquals("foo.bar.Bar${'$'}A", generatedFile.name)
        assertEquals("foo.bar.Bar${'$'}BarImpl\n", generatedFile.readText())
    }

    @Test
    fun multipleClassesWorks() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl : Bar
            |
            |interface Foo : Bar
            |
            |@ServiceLoader(Bar::class)
            |class FooImpl: Foo
            """.trimMargin(),
        )
        val result = jvmCompile(source)
        assertEquals(ExitCode.OK, result.exitCode, result.messages)
        val generatedFile = result.sourcesGeneratedBySymbolProcessor.single()

        assertEquals("foo.bar.Bar", generatedFile.name)
        assertEquals("foo.bar.BarImpl\nfoo.bar.FooImpl\n", generatedFile.readText())
    }

    @Test
    fun inheritedSuperTypeWorks() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |interface Baz : Bar
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl : Baz
            """.trimMargin(),
        )
        val result = jvmCompile(source)
        assertEquals(ExitCode.OK, result.exitCode, result.messages)
        val generatedFile = result.sourcesGeneratedBySymbolProcessor.single()

        assertEquals("foo.bar.Bar", generatedFile.name)
        assertEquals("foo.bar.BarImpl\n", generatedFile.readText())
    }

    @Test
    fun noSuperTypeFails() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl
            """.trimMargin(),
        )
        val error = assertFailsWith<IllegalArgumentException> {
            jvmCompile(source)
        }
        assertTrue("BarImpl does not implement or inherit Bar." in error.message!!)
    }

    @Test
    fun noPublicZeroArgConstructorFails() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl(val s: String) : Bar
            """.trimMargin(),
        )
        val error = assertFailsWith<IllegalArgumentException> {
            jvmCompile(source)
        }
        assertTrue("BarImpl does not have a public zero arg constructor." in error.message!!)
    }

    @Test
    fun noPublicConstructorFails() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl private constructor() : Bar
            """.trimMargin(),
        )
        val error = assertFailsWith<IllegalArgumentException> {
            jvmCompile(source)
        }
        assertTrue("BarImpl does not have a public zero arg constructor." in error.message!!)
    }

    @Test
    fun localClassFails() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |fun main() {
            |  @ServiceLoader(Bar::class)
            |  class BarImpl : Bar
            |}
            """.trimMargin(),
        )
        val error = assertFailsWith<IllegalArgumentException> {
            jvmCompile(source)
        }
        assertTrue("BarImpl is local." in error.message!!)
    }

    @Test
    fun abstractClassFails() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |abstract class BarImpl : Bar
            |
            """.trimMargin(),
        )
        val error = assertFailsWith<IllegalArgumentException> {
            jvmCompile(source)
        }
        assertTrue("BarImpl is abstract." in error.message!!)
    }

    @Test
    fun objectFails() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |object BarImpl : Bar
            |
            """.trimMargin(),
        )
        val error = assertFailsWith<IllegalArgumentException> {
            jvmCompile(source)
        }
        assertTrue("BarImpl is not a class." in error.message!!)
    }

    @Test
    fun interfaceFails() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |interface BarImpl : Bar
            |
            """.trimMargin(),
        )
        val error = assertFailsWith<IllegalArgumentException> {
            jvmCompile(source)
        }
        assertTrue("BarImpl is abstract." in error.message!!)
        assertTrue("BarImpl does not have a public zero arg constructor." in error.message!!)
    }
}
