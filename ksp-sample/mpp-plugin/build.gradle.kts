plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("app.softwork.serviceloader")
}

kotlin {
    linuxX64()

    jvm() 
    
    jvm("foo") {
        attributes {
            // https://youtrack.jetbrains.com/issue/KT-55751
            val KT_55751 = Attribute.of("KT_55751", String::class.java)
            attribute(KT_55751, "foo")
        }
    }
    jvm("bar") {
        attributes {
            // https://youtrack.jetbrains.com/issue/KT-55751
            val KT_55751 = Attribute.of("KT_55751", String::class.java)
            attribute(KT_55751, "bar")
        }
    }

    sourceSets {
        named("fooMain") {
            dependencies {
                implementation(projects.lib)
            }
        }
        named("barMain") {
            dependencies {
                implementation(projects.lib)
            }
        }
    }
}
