plugins {
    kotlin("jvm")
    id("myPublish")
}


kotlin {
    jvmToolchain(8)
    explicitApi()
    target.compilations.configureEach {
        compilerOptions.configure {
            allWarningsAsErrors.set(true)
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}
