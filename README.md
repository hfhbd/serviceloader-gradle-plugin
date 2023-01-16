# serviceloader-gradle-plugin

A Gradle plugin generating and validating your service providers.

## Usage

```kotlin
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
