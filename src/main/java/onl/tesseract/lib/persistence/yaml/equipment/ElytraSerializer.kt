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
        elytra.boostChargeLevel = yaml.getInt("boostChargeLvl")
        elytra.recoveryLevel = yaml.getInt("recoveryLvl")

        elytra.currentCharges = yaml.getInt("currentCharges", Elytra.getBoostCount(elytra.boostChargeLevel))
        elytra.rechargeProgress = yaml.getDouble("rechargeProgress", 0.0)
        return elytra
    }

    override fun serialize(value: Elytra): YamlConfiguration {
        val yaml = YamlConfiguration()
        super.writeGenericProps(yaml, value)
        yaml["autoGlide"] = value.autoGlide
        yaml["protectionLvl"] = value.protectionLevel
        yaml["speedLvl"] = value.speedLevel
        yaml["boostChargeLvl"] = value.boostChargeLevel
        yaml["recoveryLvl"] = value.recoveryLevel

        yaml["currentCharges"] = value.currentCharges
        yaml["rechargeProgress"] = value.rechargeProgress
        return yaml
    }
}