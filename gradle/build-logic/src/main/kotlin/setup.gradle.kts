plugins {
    kotlin("jvm")
    id("publishing")
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
