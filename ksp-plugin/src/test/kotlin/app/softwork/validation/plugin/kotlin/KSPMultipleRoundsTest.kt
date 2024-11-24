package app.softwork.validation.plugin.kotlin

import app.softwork.serviceloader.ksp.ServiceLoaderProvider
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.sourcesGeneratedBySymbolProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCompilerApi
@KspExperimental
class KSPMultipleRoundsTest {
    @Test
    fun simpleWorks() {
        val source = SourceFile.kotlin(
            "main.kt",
            """
            |package foo.bar
            |
            |import app.softwork.serviceloader.ServiceLoader
            |
            |@ServiceLoader(Bar::class)
            |class BarImpl : Bar
            """.trimMargin(),
        )

        val result = KotlinCompilation().apply {
            sources = arrayOf(source).toList()
            configureKsp(useKsp2 = true) {
                symbolProcessorProviders.addAll(
                    listOf(
                        ServiceLoaderProvider(),
                        InterfaceGeneratorProvider,
                    )
                )
            }
            inheritClassPath = true
        }.compile()

        assertEquals(ExitCode.OK, result.exitCode, result.messages)

        val generatedFile = result.sourcesGeneratedBySymbolProcessor.toList()
        assertEquals("foo.bar.Bar", generatedFile[0].name)
        assertEquals("foo.bar.BarImpl\n", generatedFile[0].readText())
    }
}

private object InterfaceGeneratorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) = InterfaceGenerator(environment.codeGenerator)
}

private class InterfaceGenerator(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val ksName = resolver.getKSNameFromString("foo.bar.Bar")
        if (resolver.getClassDeclarationByName(ksName) == null) {
            codeGenerator.createNewFile(
                fileName = "Bar",
                packageName = "foo.bar",
                dependencies = Dependencies(true),
            ).bufferedWriter().use {
                it.appendLine("package foo.bar")
                it.appendLine("interface Bar")
            }
        }

        return emptyList()
    }

}
