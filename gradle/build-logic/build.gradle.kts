plugins {
    `kotlin-dsl`
}

dependencies { 
    implementation(libs.kotlin.gradlePlugin)
}

kotlin.jvmToolchain(8)
