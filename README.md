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

### Use with ksp

Apply the ksp plugin.

```kotlin
// build.gradle.kts

plugins {
    id("com.google.devtools.ksp") version "1.8.20-1.0.10"
    id("app.softwork.serviceloader") version "LATEST"
}
```

### Use as Kotlin compiler plugin

Apply the Kotlin compiler plugin.

```kotlin
// build.gradle.kts

plugins {
    id("app.softwork.serviceloader-compiler") version "LATEST"
}
```

> [!NOTE]
> The Kotlin compiler plugin does not support Java source code. In this case, you need to use the ksp plugin.

You might also want to enable the compiler plugin in IntelliJ by setting `kotlin.k2.only.bundled.compiler.plugins.enabled` to `false` in the registry.

## Annotate the code

And use the `app.softwork.serviceloader.ServiceLoader` annotation:

```kotlin
import app.softwork.serviceloader.ServiceLoader

interface Provider

@ServiceLoader(Provider::class)
class Impl : Provider
```

## License

Apache 2.0
