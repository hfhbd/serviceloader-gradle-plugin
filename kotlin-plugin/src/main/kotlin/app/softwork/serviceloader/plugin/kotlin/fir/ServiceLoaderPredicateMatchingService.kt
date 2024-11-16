package app.softwork.serviceloader.plugin.kotlin.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.utils.AbstractSimpleClassPredicateMatchingService
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal class ServiceLoaderPredicateMatchingService(
    session: FirSession,
) : AbstractSimpleClassPredicateMatchingService(session) {

    override val predicate = DeclarationPredicate.create {
        annotated(serviceLoaderFq)
    }

    companion object {
        val serviceLoaderFq = FqName("app.softwork.serviceloader.ServiceLoader")
        val serviceLoaderClass = ClassId.topLevel(serviceLoaderFq)
        val forClass = Name.identifier("forClass")

        val FirSession.serviceLoaderPredicateMatchingService: ServiceLoaderPredicateMatchingService by FirSession.sessionComponentAccessor()
    }
}
