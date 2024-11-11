package app.softwork.serviceloader.plugin.kotlin.ir

import app.softwork.serviceloader.plugin.kotlin.fir.ServiceLoaderPredicateMatchingService.Companion.forClass
import app.softwork.serviceloader.plugin.kotlin.fir.ServiceLoaderPredicateMatchingService.Companion.serviceLoaderFq
import org.jetbrains.kotlin.ir.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.*

internal data object WriteServiceLoaderVisitor : IrElementVisitor<Unit, MutableMap<String, MutableList<String>>> {

    override fun visitElement(
        element: IrElement,
        data: MutableMap<String, MutableList<String>>,
    ) {
        element.acceptChildren(this, data)
    }

    override fun visitClass(
        declaration: IrClass,
        data: MutableMap<String, MutableList<String>>,
    ) {
        val serviceLoaderAnnotation = declaration.getAnnotation(serviceLoaderFq) ?: return

        val declarationFq = declaration.kotlinFqName
        val forClassRef = (serviceLoaderAnnotation.getValueArgument(name = forClass)!! as IrClassReference)
        val providerFq = forClassRef.classType.classFqName!!

        data.computeIfAbsent(providerFq.asString()) { mutableListOf() }.add(declarationFq.asString())
    }
}
