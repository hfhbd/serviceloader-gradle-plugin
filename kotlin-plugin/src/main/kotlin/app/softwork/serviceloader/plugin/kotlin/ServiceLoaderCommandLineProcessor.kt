package app.softwork.serviceloader.plugin.kotlin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.io.File

public class ServiceLoaderCommandLineProcessor : CommandLineProcessor {
    internal companion object {
        private const val OPTION_OUTPUT_DIR = "outputDir"

        val OPTION_OUTPUT_ARG = CompilerConfigurationKey<File>(OPTION_OUTPUT_DIR)
    }

    override val pluginId: String = "app.softwork.serviceloader"

    override val pluginOptions: List<CliOption> = listOf(
        CliOption(
            optionName = OPTION_OUTPUT_DIR,
            valueDescription = "output directory",
            description = "the output directory to store the service files",
            required = true,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            OPTION_OUTPUT_DIR -> configuration.put(OPTION_OUTPUT_ARG, File(value))
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}
