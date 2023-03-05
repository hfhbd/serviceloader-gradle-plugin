package app.softwork.serviceloader.ksp

import app.softwork.serviceloader.*
import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

public class ServiceLoaderPlugin(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val providers = mutableMapOf<String, MutableList<KSClassDeclaration>>()

        for (annotatedClass in resolver.getSymbolsWithAnnotation(ServiceLoader::class.qualifiedName!!)) {
            if (annotatedClass is KSClassDeclaration) {
                for (anno in annotatedClass.annotations) {
                    // TODO: use fqn
                    if (anno.shortName.getShortName() == ServiceLoader::class.simpleName) {
                        val provider = anno.arguments.single().value
                        val providerDec = requireNotNull(resolver.getClassDeclarationByName(provider.toString())) {
                            "Class $provider not found."
                        }

                        require(annotatedClass.getAllSuperTypes().any {
                            it.declaration == providerDec
                        }) {
                            "Class $annotatedClass does not implement or inherit $provider."
                        }
                        require(annotatedClass.getConstructors().any {
                            it.isPublic() && it.parameters.isEmpty()
                        }) {
                            "Class $annotatedClass does not have a public zero arg constructor."
                        }
                        require(!annotatedClass.isAbstract()) {
                            "Class $annotatedClass is abstract."
                        }
                        requireNotNull(annotatedClass.qualifiedName) {
                            "Class $annotatedClass is local."
                        }
                        val providerName = providerDec.qualifiedName!!.asString()
                        val found = providers[provider.toString()]

                        providers[providerName] = if (found == null) {
                            mutableListOf(annotatedClass)
                        } else {
                            found.add(annotatedClass)
                            found
                        }
                    }
                }
            }
        }
        for ((provider, classes) in providers) {
            codeGenerator.createNewFileByPath(
                Dependencies(false, sources = classes.map { it.containingFile!! }.toTypedArray()),
                "META-INF/services/$provider",
                extensionName = ""
            ).bufferedWriter().use {
                for (impl in classes) {
                    it.appendLine(impl.qualifiedName!!.asString())
                }
            }
        }
        return emptyList()
    }
}
