package onl.tesseract.lib.persistantcontainer

import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class NamedspacedKeyProvider(private val plugin: Plugin) {

    fun get(key: String): NamespacedKey {
        return NamespacedKey(plugin, key)
    }

    fun getString(meta: ItemMeta, key: String): String? {
        return meta.persistentDataContainer.get(get(key), PersistentDataType.STRING)
    }
}