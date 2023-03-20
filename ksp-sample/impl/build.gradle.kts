plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":lib"))
    compileOnly("app.softwork.serviceloader:ksp-annotation")
    ksp("app.softwork.serviceloader:ksp-plugin")
}
