package onl.tesseract.lib.util

import org.bukkit.configuration.ConfigurationSection

/**
 * A class that can be serialized/deserialized to/from a [ConfigurationSection]
 *
 * **Serialization**
 *
 * The interface defines a [TYamlSerializable.serialize]. Implementations should create a new ConfigurationSection, fill
 * it with the wanted fields and return it. To save nested list of objects also annotated with [TYamlSerializable],
 * [ConfigurationSectionUtils.setSectionList] can be used
 *
 * **Deserialization**
 *
 * Implementing classes should contain a *T deserialize(ConfigurationSection)* static method, taking a
 * ConfigurationSection in parameters and returning a new class instance. To load a nested list of objects also
 * annotated with [TYamlSerializable], [ConfigurationSectionUtils.getSectionList] can be used
 *
 */
fun interface TYamlSerializable {

    fun serialize(): ConfigurationSection
}