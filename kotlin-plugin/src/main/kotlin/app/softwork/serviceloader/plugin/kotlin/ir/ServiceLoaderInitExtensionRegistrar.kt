package app.softwork.serviceloader.plugin.kotlin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

internal class ServiceLoaderInitExtensionRegistrar(private val writeFile: (name: String, input: String) -> Unit) :
    IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val classes = mutableMapOf<String, MutableList<String>>()
        moduleFragment.accept(WriteServiceLoaderVisitor, classes)
        for ((classes, types) in classes) {
            writeFile(classes, types.joinToString(separator = "\n"))
        }
    }
}
