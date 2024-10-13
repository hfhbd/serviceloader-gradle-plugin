package app.softwork.serviceloader.plugin.kotlin

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal class WriteServiceLoaderVisitor(
    private val writeFile: (name: String, input: String) -> Unit,
) : IrElementVisitorVoid {

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    private val serviceLoaderAnnotationFqName = FqName("app.softwork.serviceloader.ServiceLoader")

    override fun visitClass(declaration: IrClass) {
        val serviceLoaderAnnotation =
            declaration.getAnnotation(serviceLoaderAnnotationFqName) ?: return
        val declarationFq = declaration.kotlinFqName
        val providerDec =
            (serviceLoaderAnnotation.getValueArgument(name = Name.identifier("forClass"))!! as IrClassReference).classType
        val providerClass = providerDec.getClass()!!

        val providerFq = providerClass.kotlinFqName

        require(declaration.isSubclassOf(providerClass)) {
            "Class ${declarationFq.asString()} does not implement or inherit ${providerFq.asString()}."
        }
        require(!declaration.isLocal) {
            "Class ${declarationFq.asString()} is local."
        }
        @OptIn(UnsafeDuringIrConstructionAPI::class)
        require(declaration.constructors.any {
            it.visibility.isPublicAPI && it.valueParameters.isEmpty()
        }) {
            "Class ${declarationFq.asString()} does not have a public zero arg constructor."
        }
        require(declaration.modality != Modality.ABSTRACT) {
            "Class ${declarationFq.asString()} is abstract."
        }
        writeFile(providerFq.asString(), declarationFq.asString())
    }
}
