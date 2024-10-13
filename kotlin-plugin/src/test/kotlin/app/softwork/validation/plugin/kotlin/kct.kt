package app.softwork.validation.plugin.kotlin

import app.softwork.serviceloader.plugin.kotlin.ServiceLoaderCompilerPluginRegistrar.Companion.registerServiceLoader
import app.softwork.serviceloader.plugin.kotlin.ServiceLoaderInitExtensionRegistrar
import com.tschuchort.compiletesting.DiagnosticSeverity
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.test.assertEquals

fun jvmCompile(vararg files: SourceFile, writeFile: (String, String) -> Unit) {
    val result = KotlinCompilation()
        .apply {
            sources = files.toList()
            compilerPluginRegistrars = listOf(ValidationCompilerPluginRegistrarTest(writeFile))
            inheritClassPath = true
        }
        .compile()
    if (result.exitCode != ExitCode.OK) {
     throw IllegalArgumentException(result.messages)
    }
}

private class ValidationCompilerPluginRegistrarTest(
    private val writeFile: (name: String, input: String) -> Unit,
) : CompilerPluginRegistrar() {
    override val supportsK2 = true
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        registerServiceLoader(writeFile)
    }
}
