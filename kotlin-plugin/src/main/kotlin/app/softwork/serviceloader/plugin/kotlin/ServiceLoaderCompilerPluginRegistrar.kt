package app.softwork.serviceloader.plugin.kotlin

import app.softwork.serviceloader.plugin.kotlin.ServiceLoaderCommandLineProcessor.Companion.OPTION_OUTPUT_ARG
import app.softwork.serviceloader.plugin.kotlin.fir.ServiceLoaderFirExtensionRegistrar
import app.softwork.serviceloader.plugin.kotlin.ir.ServiceLoaderInitExtensionRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import java.io.File

public class ServiceLoaderCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        registerServiceLoaderFIR()

        val outputDir = configuration.get(OPTION_OUTPUT_ARG)
        if (outputDir != null) {
            val serviceDir = File(outputDir, "META-INF/services")
            serviceDir.mkdirs()

            val generateFile = { fileName: String, fileContent: String ->
                File(serviceDir, fileName).writeText(fileContent, charset = Charsets.UTF_8)
            }

            registerServiceLoaderIR(generateFile)
        }
    }

    internal companion object {
        fun ExtensionStorage.registerServiceLoaderFIR() {
            FirExtensionRegistrarAdapter.registerExtension(ServiceLoaderFirExtensionRegistrar)
        }

        fun ExtensionStorage.registerServiceLoaderIR(writeFile: (String, String) -> Unit) {
            val extension = ServiceLoaderInitExtensionRegistrar(writeFile)
            IrGenerationExtension.registerExtension(extension)
        }
    }
}
