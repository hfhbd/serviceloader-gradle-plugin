package app.softwork.serviceloader.plugin.kotlin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

internal class ServiceLoaderInitExtensionRegistrar(
    private val writeFile: (fileName: String, fileContent: String) -> Unit,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val classes = mutableMapOf<String, MutableList<String>>()
        moduleFragment.accept(WriteServiceLoaderVisitor, classes)
        for ((service, providers) in classes) {
            writeFile(service, providers.joinToString(separator = "\n", postfix = "\n"))
        }
    }
}
