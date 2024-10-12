package app.softwork.validation.plugin.kotlin

import com.tschuchort.compiletesting.*
import kotlin.test.*

class CompilerTest {
    @Test
    fun success() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |import app.softwork.serviceloader.ServiceLoader
            |
            |interface Bar
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl : Bar
            """.trimMargin(),
        )
        var called = false
        jvmCompile(source) { name, input ->
            called = true
            assertEquals("Bar", name)
            assertEquals("BarImpl", input)
        }
        assertTrue(called)
    }
}
