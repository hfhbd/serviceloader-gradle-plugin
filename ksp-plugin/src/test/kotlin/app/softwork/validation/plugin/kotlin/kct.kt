package app.softwork.validation.plugin.kotlin

import app.softwork.serviceloader.ksp.ServiceLoaderProvider
import com.google.devtools.ksp.KspExperimental
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@ExperimentalCompilerApi
@KspExperimental
fun jvmCompile(vararg files: SourceFile): JvmCompilationResult {
    val result = KotlinCompilation()
        .apply {
            sources = files.toList()
            configureKsp(useKsp2 = true) {
                symbolProcessorProviders.add(ServiceLoaderProvider())
            }
            inheritClassPath = true
        }
        .compile()
    require(result.exitCode == ExitCode.OK) {
        result.messages
    }
    return result
}
