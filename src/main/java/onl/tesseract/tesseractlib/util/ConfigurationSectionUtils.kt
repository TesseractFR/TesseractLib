package onl.tesseract.tesseractlib.util

import org.bukkit.configuration.ConfigurationSection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

class ConfigurationSectionUtils {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ConfigurationSectionUtils::class.java)

        /**
         * Save a list of complex objects. Use [ConfigurationSectionUtils.getSectionList] to deserialize.
         * @param T Type of the objects to save
         */
        fun <T : TYamlSerializable> setSectionList(
            config: ConfigurationSection,
            name: String,
            sectionList: List<T>
        ) {

            for (i in sectionList.indices) {
                config["$name.$i"] = sectionList[i].serialize()
            }
        }

        /**
         * Loads a list of complex objects that have been saved using [ConfigurationSectionUtils.setSectionList]
         */
        fun <T : TYamlSerializable> getSectionList(
            config: ConfigurationSection,
            name: String,
            clazz: Class<T>
        ): List<T> {
            val listSection = config.getConfigurationSection(name) ?: return listOf()

            val resultList: MutableList<T> = mutableListOf()
            val deserializeMethod: Method
            try {
                deserializeMethod = clazz.getDeclaredMethod("deserialize", ConfigurationSection::class.java)
            } catch (e: NoSuchMethodException) {
                logger.error("No deserialize() method for class ${clazz.name}")
                return emptyList()
            }

            listSection.getKeys(false).forEach {
                val elementSection: ConfigurationSection = listSection.getConfigurationSection(it) ?: return@forEach

                val obj: Any = deserializeMethod.invoke(null, elementSection)
                if (!clazz.isInstance(obj)) {
                    logger.error("Wrong return type for deserialize() method for class ${clazz.name}")
                    return emptyList()
                }
                resultList.add(clazz.cast(obj))
            }

            return resultList
        }
    }
}

/**
 * Save a list of complex objects. Use [ConfigurationSection.getSectionList] to deserialize.
 * @param T Type of the objects to save
 */
fun <T : TYamlSerializable> ConfigurationSection.setSectionList(name: String, sectionList: List<T>) {
    return ConfigurationSectionUtils.setSectionList(this, name, sectionList)
}

/**
 * Loads a list of complex objects that have been saved using [ConfigurationSection.setSectionList]
 */
fun <T : TYamlSerializable> ConfigurationSection.getSectionList(name: String, clazz: Class<T>): List<T> {
    return ConfigurationSectionUtils.getSectionList(this, name, clazz)
}