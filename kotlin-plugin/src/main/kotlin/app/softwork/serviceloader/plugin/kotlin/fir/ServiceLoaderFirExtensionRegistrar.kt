package app.softwork.serviceloader.plugin.kotlin.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

internal data object ServiceLoaderFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::ServiceLoaderPredicateMatchingService
        +::ServiceLoaderCheckerExtension
    }
}
