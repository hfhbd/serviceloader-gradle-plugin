plugins {
    setup
}

dependencies {
    implementation(projects.kspAnnotation)
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.10-1.0.9")
}
