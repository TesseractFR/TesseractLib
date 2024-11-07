package onl.tesseract.lib.persistantcontainer

import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

class NamedspacedKeyProvider(private val plugin: Plugin) {

    fun get(key: String): NamespacedKey {
        return NamespacedKey(plugin, key)
    }
}