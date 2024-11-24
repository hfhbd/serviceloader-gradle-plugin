plugins {
    id("kotlinSetup")
    id("maven-publish")
}

publishing.publications.register<MavenPublication>("mavenJava") {
    from(components["java"])
}

dependencies {
    compileOnly(libs.ksp.api)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinCompilerTester.ksp)
    testImplementation(libs.ksp.api)
    testImplementation(projects.runtime)
}
