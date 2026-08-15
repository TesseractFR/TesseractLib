package onl.tesseract.lib.persistence.yaml.equipment

import onl.tesseract.lib.event.equipment.invocable.Boussole
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration

class BoussoleSerializer : InvocableGenericSerializer<Boussole>() {
    override fun deserialize(yaml: ConfigurationSection): Boussole {
        return Boussole(
            parsePlayerID(yaml),
            parseInvoked(yaml),
            parseHandSlot(yaml),
        )
    }

    override fun serialize(value: Boussole): YamlConfiguration {
        val yamlConfiguration = YamlConfiguration()
        super.writeGenericProps(yamlConfiguration, value)
        return yamlConfiguration
    }
}