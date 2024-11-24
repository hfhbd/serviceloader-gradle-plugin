package app.softwork.serviceloader.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*

@KspExperimental
public class ServiceLoaderProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): ServiceLoaderPlugin =
        ServiceLoaderPlugin(environment.codeGenerator, environment.logger)
}
