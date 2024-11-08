package onl.tesseract.lib.persistence.yaml

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

interface YamlSerializer<T> {

    fun serialize(value: T): YamlConfiguration

    fun deserialize(yaml: ConfigurationSection): T

    fun saveToFile(value: T, file: File) {
        serialize(value).save(file)
    }

    fun loadFromFile(file: File): T? {
        if (!file.exists()) return null
        return deserialize(YamlConfiguration.loadConfiguration(file))
    }
}