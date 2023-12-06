pluginManagement {
    includeBuild("gradle/build-logic")
    repositories { 
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins { 
    id("myRepos")
    id("com.gradle.enterprise") version "3.16"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        if (System.getenv("CI") != null) {
            publishAlways()
            tag("CI")
        }
    }
}

rootProject.name = "serviceloader-gradle-plugin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(":gradle-plugin")

include(":ksp-annotation")
include(":ksp-plugin")
