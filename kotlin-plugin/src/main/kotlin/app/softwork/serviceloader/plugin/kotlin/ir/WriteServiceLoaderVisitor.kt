package app.softwork.serviceloader.plugin.kotlin.ir

import app.softwork.serviceloader.plugin.kotlin.fir.ServiceLoaderPredicateMatchingService.Companion.forClass
import app.softwork.serviceloader.plugin.kotlin.fir.ServiceLoaderPredicateMatchingService.Companion.serviceLoaderFq
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isLocal
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

internal object WriteServiceLoaderVisitor : IrElementVisitor<Unit, MutableMap<String, MutableList<String>>> {

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
        val serviceLoaderAnnotation =
            declaration.getAnnotation(serviceLoaderFq) ?: return
        val declarationFq = declaration.kotlinFqName
        val providerDec =
            (serviceLoaderAnnotation.getValueArgument(name = forClass)!! as IrClassReference).classType
        val providerClass = providerDec.getClass()!!

        val providerFq = providerClass.kotlinFqName

        require(declaration.isSubclassOf(providerClass)) {
            "${declarationFq.asString()} does not implement or inherit ${providerFq.asString()}."
        }
        require(!declaration.isLocal) {
            "${declarationFq.asString()} is local."
        }
        @OptIn(UnsafeDuringIrConstructionAPI::class)
        require(declaration.constructors.any {
            it.visibility.isPublicAPI && it.valueParameters.isEmpty()
        }) {
            "${declarationFq.asString()} does not have a public zero arg constructor."
        }
        require(declaration.modality != Modality.ABSTRACT) {
            "${declarationFq.asString()} is abstract."
        }

        data.computeIfAbsent(providerFq.asString()) { mutableListOf() }.add(declarationFq.asString())
    }
}
