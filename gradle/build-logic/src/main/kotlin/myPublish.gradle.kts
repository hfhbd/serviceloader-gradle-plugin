plugins {
    id("maven-publish")
    id("signing")
    id("io.github.hfhbd.mavencentral")
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("app.softwork ServiceLoader Gradle Plugin")
            description.set("A Gradle plugin to generate and validate service loaders")
            url.set("https://github.com/hfhbd/serviceloader-gradle-plugin")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("hfhbd")
                    name.set("Philip Wedemann")
                    email.set("mybztg+mavencentral@icloud.com")
                }
            }
            scm {
                connection.set("https://github.com/hfhbd/serviceloader-gradle-plugin.git")
                developerConnection.set("scm:git://github.com/hfhbd/serviceloader-gradle-plugin.git")
                url.set("https://github.com/hfhbd/serviceloader-gradle-plugin")
            }
        }
    }
}

signing {
    val signingKey = providers.gradleProperty("signingKey")
    if (signingKey.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), providers.gradleProperty("signingPassword").get())
        sign(publishing.publications)
    }
}

// https://youtrack.jetbrains.com/issue/KT-46466
val signingTasks = tasks.withType<Sign>()
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(signingTasks)
}
