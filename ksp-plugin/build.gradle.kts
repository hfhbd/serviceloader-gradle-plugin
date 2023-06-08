plugins {
    id("kotlinSetup")
    id("maven-publish")
}

publishing.publications.register<MavenPublication>("mavenJava") {
    from(components["java"])
}

dependencies {
    implementation(projects.kspAnnotation)
    implementation(libs.ksp.api)
}
