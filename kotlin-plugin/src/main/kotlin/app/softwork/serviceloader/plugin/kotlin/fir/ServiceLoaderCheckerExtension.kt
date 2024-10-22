package app.softwork.serviceloader.plugin.kotlin.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

internal class ServiceLoaderCheckerExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    val checker: FirRegularClassChecker = ServiceLoaderClassChecker()
    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val regularClassCheckers = setOf(checker)
    }
}
