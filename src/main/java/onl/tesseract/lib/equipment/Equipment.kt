package onl.tesseract.lib.equipment

import org.bukkit.inventory.EquipmentSlot
import java.util.*

class Equipment(
    val playerUUID: UUID,
    val invocables: MutableCollection<Invocable> = mutableListOf(),
) {

    fun getInvoked(): Collection<Invocable> {
        return invocables.filter { it.isInvoked }
    }

    operator fun get(slot: EquipmentSlot): Invocable? {
        return invocables.find { it.slotType == slot && it.excludeOthers }
    }

    fun getAll(slot: EquipmentSlot): List<Invocable> {
        return invocables.filter { it.slotType == slot }
    }

    fun <T : Invocable> get(type: Class<T>): T? {
        return invocables.find { type.isInstance(it) }
            ?.let { type.cast(it) }
    }

    fun get(uniqueName: String): Invocable? {
        return invocables.find { it.uniqueName == uniqueName }
    }
}