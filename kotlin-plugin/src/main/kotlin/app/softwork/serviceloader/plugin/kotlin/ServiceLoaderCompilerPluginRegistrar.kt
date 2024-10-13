package app.softwork.serviceloader.plugin.kotlin

import app.softwork.serviceloader.plugin.kotlin.ServiceLoaderCommandLineProcessor.Companion.OPTION_OUTPUT_ARG
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.File

public class ServiceLoaderCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val outputDir = configuration.getNotNull(OPTION_OUTPUT_ARG)
        val serviceDir = File(outputDir, "META-INF/services")
        serviceDir.mkdirs()

        val generateFile = { name: String, input: String ->
            File(serviceDir, name).writeText(input)
        }

        registerServiceLoader(generateFile)
    }

    internal companion object {
        fun ExtensionStorage.registerServiceLoader(writeFile: (String, String) -> Unit) {
            val extension = ServiceLoaderInitExtensionRegistrar(writeFile)
            IrGenerationExtension.registerExtension(extension)
            registerDisposable(extension)
        }
    }
}
