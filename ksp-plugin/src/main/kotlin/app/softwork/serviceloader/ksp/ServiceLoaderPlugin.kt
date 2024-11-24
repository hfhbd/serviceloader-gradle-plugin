package app.softwork.serviceloader.ksp

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

@KspExperimental
public class ServiceLoaderPlugin(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private val providers = mutableMapOf<String, MutableList<String>>()

    override fun finish() {
        for ((binaryProviderName, binaryImplementations) in providers) {
            codeGenerator.createNewFileByPath(
                dependencies = Dependencies(false),
                path = "META-INF/services/$binaryProviderName",
                extensionName = "",
            ).bufferedWriter(charset = Charsets.UTF_8).use {
                for (binaryImplementation in binaryImplementations) {
                    it.appendLine(binaryImplementation)
                }
            }
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val notFound = mutableListOf<KSAnnotated>()

        annotated@ for (annotatedClass in resolver.getSymbolsWithAnnotation(
            annotationName = "app.softwork.serviceloader.ServiceLoader",
            inDepth = true,
        )) {
            if (annotatedClass is KSClassDeclaration) {
                for (anno in annotatedClass.annotations) {
                    if (anno.shortName.getShortName() == "ServiceLoader") {
                        if (annotatedClass.classKind != ClassKind.CLASS) {
                            logger.error("$annotatedClass is not a class.", annotatedClass)
                        }
                        if (annotatedClass.getConstructors().none {
                                it.isPublic() && it.parameters.isEmpty()
                            }
                        ) {
                            logger.error("$annotatedClass does not have a public zero arg constructor.", annotatedClass)
                        }
                        if (annotatedClass.isAbstract()) {
                            logger.error("$annotatedClass is abstract.", annotatedClass)
                        }
                        if (annotatedClass.qualifiedName == null) {
                            logger.error("$annotatedClass is local.", annotatedClass)
                        }
                        val provider = anno.arguments.single().value as KSType
                        if (provider.isError) {
                            notFound.add(annotatedClass)
                            continue@annotated
                        }

                        val providerDec = provider.declaration

                        if (annotatedClass.getAllSuperTypes().none {
                                it.declaration == providerDec
                            }
                        ) {
                            logger.error("$annotatedClass does not implement or inherit $provider.", annotatedClass)
                        }
                        val providerName = resolver.getBinaryName(providerDec)
                        val found = providers[providerName]

                        providers[providerName] = if (found == null) {
                            mutableListOf(resolver.getBinaryName(annotatedClass))
                        } else {
                            found.add(resolver.getBinaryName(annotatedClass))
                            found
                        }
                    }
                }
            }
        }
        return notFound
    }

    private fun Resolver.getBinaryName(klass: KSDeclaration): String {
        val jvmName = mapToJvmSignature(klass)!!
        val binaryName = jvmName.drop(1).replace("/", ".").dropLast(1)
        return binaryName
    }
}
