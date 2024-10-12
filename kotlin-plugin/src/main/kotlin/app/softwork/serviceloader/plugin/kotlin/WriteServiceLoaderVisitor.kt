package app.softwork.serviceloader.plugin.kotlin

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.getValueArgument
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
        val providerDec = (serviceLoaderAnnotation.getValueArgument(name = Name.identifier("forClass"))!! as IrClassReference).classType

        require(declaration.superTypes.any {
            it == providerDec
        }) {
            "Class $declaration does not implement or inherit $providerDec."
        }
        @OptIn(UnsafeDuringIrConstructionAPI::class)
        require(declaration.constructors.any {
            it.visibility.isPublicAPI && it.valueParameters.isEmpty()
        }) {
            "Class $declaration does not have a public zero arg constructor."
        }
        require(declaration.modality != Modality.ABSTRACT) {
            "Class $declaration is abstract."
        }
        val declarationFq = requireNotNull(declaration.fqNameWhenAvailable) {
            "Class $declaration is local."
        }

        writeFile(providerDec.classFqName!!.asString(), declarationFq.asString())
    }
}
