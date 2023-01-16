# serviceloader-gradle-plugin

A Gradle plugin generating and validating your service providers.

## Usage
This plugin is uploaded to mavenCentral `app.softwork:serviceloader-gradle-plugin:LATEST`, so you need to add mavenCentral to your plugin repositories:
```kotlin
// settings.gradle.kts
pluginManagement {
  repositories {
    mavenCentral()
  }
}
```

```kotlin
// build.gradle.kts

plugins {
    id("app.softwork.serviceloader") version "LATEST" 
}

serviceLoaders.register("my.custom.Serviceloader") {
    implementationClasses.add("my.custom.ServiceProvider")
}
```

The services and its providers will be validated.
The services are checked to be available in the `compileClasspath`.
The implementation classes are required to provide a public zero-arg constructor.

## License

Apache 2.0
