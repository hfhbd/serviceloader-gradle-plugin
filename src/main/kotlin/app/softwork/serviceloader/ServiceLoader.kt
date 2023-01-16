package app.softwork.serviceloader

import org.gradle.api.Named
import org.gradle.api.provider.*
import javax.inject.*

public abstract class ServiceLoader @Inject constructor(private val name: String) : Named {
    override fun getName(): String = name

    public abstract val implementationClass: ListProperty<String>
}
