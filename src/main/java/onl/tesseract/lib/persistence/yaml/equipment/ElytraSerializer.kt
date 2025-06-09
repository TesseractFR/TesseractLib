package onl.tesseract.lib.persistence.yaml.equipment

import onl.tesseract.lib.event.equipment.invocable.Elytra
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration

class ElytraSerializer : InvocableGenericSerializer<Elytra>() {
    override fun deserialize(yaml: ConfigurationSection): Elytra {
        return Elytra(
            playerUUID = parsePlayerID(yaml),
            invoked = parseInvoked(yaml),
            handSlot = parseHandSlot(yaml),
            autoGlide = yaml.getBoolean("autoGlide"),
            protectionLevel = yaml.getInt("protectionLvl"),
            speedLevel = yaml.getInt("speedLvl"),
            boostChargeLevel = yaml.getInt("boostChargeLvl"),
            recoveryLevel = yaml.getInt("recoveryLvl"),
            currentCharges = yaml.getInt("currentCharges", Elytra.getBoostCount(yaml.getInt("boostChargeLvl"))),
            rechargeProgress = yaml.getDouble("rechargeProgress", 0.0)
        )
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
