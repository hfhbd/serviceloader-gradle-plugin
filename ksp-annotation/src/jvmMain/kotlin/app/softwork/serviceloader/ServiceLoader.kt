package app.softwork.serviceloader

import kotlin.reflect.*

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public actual annotation class ServiceLoader(actual val forClass: KClass<*>)
