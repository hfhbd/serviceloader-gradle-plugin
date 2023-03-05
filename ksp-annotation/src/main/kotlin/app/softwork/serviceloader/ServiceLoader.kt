package app.softwork.serviceloader

import kotlin.reflect.*

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class ServiceLoader(val forClass: KClass<*>)
