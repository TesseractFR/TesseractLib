package onl.tesseract.lib.persistence.yaml

import onl.tesseract.lib.logger.LoggerFactory
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.joml.ConfigurationException
import org.slf4j.Logger
import java.io.File

val logger: Logger = LoggerFactory.getLogger(YamlSerializer::class.java)

interface YamlSerializer<T> {

    fun serialize(value: T): YamlConfiguration

    fun deserialize(yaml: ConfigurationSection): T

    fun saveToFile(value: T, file: File) {
        serialize(value).save(file)
    }

    fun loadFromFile(file: File): T? {
        if (!file.exists()) return null
        try {
            return deserialize(YamlConfiguration.loadConfiguration(file))
        } catch (e: Exception) {
            throw ConfigurationException("Error while loading file $file", e)
        }
    }
}