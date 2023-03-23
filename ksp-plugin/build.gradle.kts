plugins {
    setup
    `maven-publish`
}

publishing.publications.register<MavenPublication>("mavenJava") {
    from(components["java"])
}

dependencies {
    implementation(projects.kspAnnotation)
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.10-1.0.9")
}
