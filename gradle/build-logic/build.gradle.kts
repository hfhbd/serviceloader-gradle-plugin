plugins {
    `kotlin-dsl`
}

dependencies { 
    implementation(libs.plugins.kotlin.jvm.toDep())
    implementation(libs.plugins.ksp.toDep())
    implementation(libs.plugins.mavencentral.toDep())
}

fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}
