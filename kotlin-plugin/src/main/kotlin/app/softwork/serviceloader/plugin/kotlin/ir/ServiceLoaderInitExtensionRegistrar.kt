package app.softwork.serviceloader.plugin.kotlin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptVoid

internal class ServiceLoaderInitExtensionRegistrar(private val writeFile: (name: String, input: String) -> Unit) :
    IrGenerationExtension, CompilerPluginRegistrar.PluginDisposable {

    private val classes = mutableMapOf<String, MutableList<String>>()

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.acceptVoid(WriteServiceLoaderVisitor { name, input ->
            classes.computeIfAbsent(name) { mutableListOf() }.add(input)
        })
    }

    override fun dispose() {
        for ((classes, types) in classes) {
            writeFile(classes, types.joinToString(separator = "\n"))
        }
    }
}
