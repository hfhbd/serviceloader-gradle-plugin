package app.softwork.serviceloader.ksp

import com.google.devtools.ksp.processing.*

public class ServiceLoaderProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): ServiceLoaderPlugin =
        ServiceLoaderPlugin(environment.codeGenerator)
}
