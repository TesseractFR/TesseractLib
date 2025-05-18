package onl.tesseract.lib.persistence.yaml.equipment

import onl.tesseract.lib.event.equipment.invocable.Elytra
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration

class ElytraSerializer : InvocableGenericSerializer<Elytra>() {
    override fun deserialize(yaml: ConfigurationSection): Elytra {
        val elytra = Elytra(
            parsePlayerID(yaml),
            parseInvoked(yaml),
            parseHandSlot(yaml),
        )
        elytra.autoGlide = yaml.getBoolean("autoGlide")
        elytra.protectionLevel = yaml.getInt("protectionLvl")
        elytra.speedLevel = yaml.getInt("speedLvl")
        elytra.topprotectionLevel = yaml.getInt("topprotectionLvl", 0)
        elytra.topspeedLevel = yaml.getInt("topspeedLvl", 0)
        if (elytra.topprotectionLevel == 0)
            elytra.topprotectionLevel = elytra.protectionLevel
        if (elytra.topspeedLevel == 0)
            elytra.topspeedLevel = elytra.speedLevel
        return elytra
    }

    override fun serialize(value: Elytra): YamlConfiguration {
        val yaml = YamlConfiguration()
        super.writeGenericProps(yaml, value)
        yaml["autoGlide"] = value.autoGlide
        yaml["protectionLvl"] = value.protectionLevel
        yaml["speedLvl"] = value.speedLevel
        yaml["topprotectionLvl"] = value.topprotectionLevel
        yaml["topspeedLvl"] = value.topspeedLevel
        return yaml
    }
}