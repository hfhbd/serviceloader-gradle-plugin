package app.softwork.serviceloader

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
public expect annotation class ServiceLoader(
    val forClass: KClass<*>
)
