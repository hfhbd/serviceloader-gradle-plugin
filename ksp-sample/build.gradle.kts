plugins {
    kotlin("jvm") version "1.8.20"
    id("com.google.devtools.ksp") version "1.8.20-RC2-1.0.9"
}

dependencies {
    compileOnly("app.softwork.serviceloader:ksp-annotation")
    ksp("app.softwork.serviceloader:ksp-plugin")
}
