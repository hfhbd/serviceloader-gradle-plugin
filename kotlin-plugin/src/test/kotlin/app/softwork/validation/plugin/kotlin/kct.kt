package app.softwork.validation.plugin.kotlin

import app.softwork.serviceloader.plugin.kotlin.ServiceLoaderCompilerPluginRegistrar.Companion.registerServiceLoaderFIR
import app.softwork.serviceloader.plugin.kotlin.ServiceLoaderCompilerPluginRegistrar.Companion.registerServiceLoaderIR
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

fun jvmCompile(vararg files: SourceFile, writeFile: (String, String) -> Unit) {
    val result = KotlinCompilation()
        .apply {
            sources = files.toList()
            compilerPluginRegistrars = listOf(ValidationCompilerPluginRegistrarTest(writeFile))
            inheritClassPath = true
        }
        .compile()
    require(result.exitCode == ExitCode.OK) {
        result.messages
    }
}

private class ValidationCompilerPluginRegistrarTest(
    private val writeFile: (name: String, input: String) -> Unit,
) : CompilerPluginRegistrar() {
    override val supportsK2 = true
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        registerServiceLoaderFIR()
        registerServiceLoaderIR(writeFile)
    }
}
