package onl.tesseract.lib.persistence.yaml.equipment

import onl.tesseract.lib.equipment.Equipment
import onl.tesseract.lib.equipment.EquipmentRepository
import onl.tesseract.lib.equipment.Invocable
import onl.tesseract.lib.persistence.yaml.YamlSerializer
import onl.tesseract.lib.repository.RepositoryWithCache
import onl.tesseract.lib.serialization.getSectionList
import onl.tesseract.lib.serialization.setSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

object EquipmentYamlRepository : EquipmentRepository, RepositoryWithCache<Equipment, UUID>() {

    override fun idOf(entity: Equipment): UUID = entity.playerUUID

    override fun read(id: UUID): Equipment? {
        return EquipmentYamlSerializer.loadFromFile(File("plugins/Tesseract/players/$id/equipment.yml"))
    }

    override fun write(entity: Equipment): Equipment {
        EquipmentYamlSerializer.saveToFile(entity, File("plugins/Tesseract/players/${entity.playerUUID}/equipment.yml"))
        return entity
    }

    fun <T : Invocable> registerTypeSerializer(type: String, serializer: YamlSerializer<T>) {
        InvocableYamlSerializer.registerTypeSerializer(type, serializer)
    }
}

object EquipmentYamlSerializer : YamlSerializer<Equipment> {
    override fun deserialize(yaml: ConfigurationSection): Equipment {
        val uuid = UUID.fromString(yaml.getString("playerUUID"))
        val invocables = yaml.getSectionList("invocables", InvocableYamlSerializer::deserialize)
        return Equipment(uuid, invocables.toMutableList())
    }

    override fun serialize(value: Equipment): YamlConfiguration {
        val yaml = YamlConfiguration()
        yaml["playerUUID"] = value.playerUUID.toString()
        yaml.setSectionList("invocables", value.invocables, InvocableYamlSerializer::serialize)
        return yaml
    }
}

object InvocableYamlSerializer : YamlSerializer<Invocable> {

    private val typeSerializers: MutableMap<String, YamlSerializer<*>> = mutableMapOf()

    fun <T : Invocable> registerTypeSerializer(type: String, serializer: YamlSerializer<T>) {
        typeSerializers[type] = serializer
    }

    override fun deserialize(yaml: ConfigurationSection): Invocable {
        val type = yaml.getString("type")
        val typeSerializer = typeSerializers[type] ?: throw IllegalStateException("No type serializer for $type")
        typeSerializer as YamlSerializer<Invocable>
        return typeSerializer.deserialize(yaml.getConfigurationSection("data")!!) as Invocable
    }

    override fun serialize(value: Invocable): YamlConfiguration {
        val yaml = YamlConfiguration()
        yaml["type"] = value.uniqueName
        val typeSerializer = typeSerializers[value.uniqueName] ?: throw IllegalStateException("No type serializer for ${value.uniqueName}")
        typeSerializer as YamlSerializer<Invocable>
        yaml["data"] = typeSerializer.serialize(value)
        return yaml
    }
}