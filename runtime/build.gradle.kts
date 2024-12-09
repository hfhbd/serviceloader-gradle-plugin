plugins {
    kotlin("multiplatform")
    id("myPublish")
}

kotlin {
    jvmToolchain(8)

    explicitApi()
    compilerOptions {
        allWarningsAsErrors.set(true)
        freeCompilerArgs.add("-Xexpect-actual-classes")
        extraWarnings.set(true)
    }

    jvm()

    js {
        nodejs()
    }

    wasmJs {
        nodejs()
    }
    wasmWasi {
        nodejs()
    }

    // tier 1
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    iosArm64()

    // tier 2
    linuxX64()
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()

    // tier 3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()
}

val emptyJar by tasks.registering(Jar::class) { }

publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(emptyJar) {
            classifier = "javadoc"
        }
    }
}
