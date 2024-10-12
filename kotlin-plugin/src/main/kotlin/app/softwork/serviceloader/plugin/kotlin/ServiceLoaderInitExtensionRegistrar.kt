package app.softwork.serviceloader.plugin.kotlin

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.visitors.*

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
