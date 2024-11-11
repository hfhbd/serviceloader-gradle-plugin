package app.softwork.serviceloader.plugin.kotlin.fir

import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.psi.KtElement

internal data object ServiceLoaderErrors {
    val SUPERTYPE_OF_CLASS_DOES_NOT_MATCH by error2<KtElement, FirRegularClassSymbol, FirRegularClassSymbol>()
    val NO_PUBLIC_CONSTRUCTOR by error1<KtElement, FirRegularClassSymbol>()
    val ABSTRACT_CLASS by error1<KtElement, FirRegularClassSymbol>()
    val LOCAL_CLASS by error1<KtElement, FirRegularClassSymbol>()

    init {
        RootDiagnosticRendererFactory.registerFactory(KtDefaultErrorMessagesServiceLoaders)
    }
}
