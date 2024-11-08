package onl.tesseract.lib.persistence.yaml.equipment

import onl.tesseract.lib.equipment.Invocable
import onl.tesseract.lib.persistence.yaml.YamlSerializer
import org.bukkit.configuration.ConfigurationSection
import java.util.*

abstract class InvocableGenericSerializer<T : Invocable> : YamlSerializer<T> {

    fun parsePlayerID(config: ConfigurationSection): UUID = UUID.fromString(config.getString("playerUUID"))
    fun parseInvoked(config: ConfigurationSection): Boolean = config.getBoolean("invoked")
    fun parseHandSlot(config: ConfigurationSection): Int = config.getInt("handSlot")

    fun writeGenericProps(config: ConfigurationSection, invocable: T) {
        config["playerUUID"] = invocable.playerUUID
        config["invoked"] = invocable.isInvoked
        config["handSlot"] = invocable.handSlot
    }
}