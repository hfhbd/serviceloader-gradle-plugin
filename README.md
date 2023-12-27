# serviceloader-gradle-plugin

A Gradle plugin generating and validating your service providers.

## Usage

This plugin is uploaded to mavenCentral `app.softwork.serviceloader:gradle-plugin:LATEST`, so you need to add
mavenCentral to your plugin repositories:

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

## Usage with ksp

Apply the ksp plugin.

```kotlin
// build.gradle.kts

plugins {
    id("com.google.devtools.ksp") version "see-ksp"
    id("app.softwork.serviceloader") version "LATEST"
}
```
And use the `app.softwork.serviceloader.ServiceLoader` annotation:
```kotlin
import app.softwork.serviceloader.ServiceLoader

interface Provider

@ServiceLoader(Provider::class)
class Impl: Provider
```

## License

Apache 2.0
