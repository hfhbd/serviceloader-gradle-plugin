package app.softwork.serviceloader.plugin.kotlin.fir

import app.softwork.serviceloader.plugin.kotlin.fir.ServiceLoaderErrors.ABSTRACT_CLASS
import app.softwork.serviceloader.plugin.kotlin.fir.ServiceLoaderErrors.NO_PUBLIC_CONSTRUCTOR
import app.softwork.serviceloader.plugin.kotlin.fir.ServiceLoaderErrors.SUPERTYPE_OF_CLASS_DOES_NOT_MATCH
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.diagnostics.rendering.*
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.DECLARATION_NAME

internal data object KtDefaultErrorMessagesServiceLoaders : BaseDiagnosticRendererFactory() {
    override val MAP = KtDiagnosticFactoryToRendererMap("ServiceLoader").apply {
        put(
            SUPERTYPE_OF_CLASS_DOES_NOT_MATCH,
            "{0} does not implement or inherit {1}.",
            DECLARATION_NAME,
            DECLARATION_NAME,
        )

        put(
            NO_PUBLIC_CONSTRUCTOR,
            "{0} does not have a public zero arg constructor.",
            DECLARATION_NAME,
        )

        put(
            ABSTRACT_CLASS,
            "{0} is abstract.",
            DECLARATION_NAME,
        )
    }
}
