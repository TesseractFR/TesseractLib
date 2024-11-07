package onl.tesseract.lib.equipment

import onl.tesseract.lib.event.EventService
import onl.tesseract.lib.event.equipment.PlayerInvocableInvokeEvent
import onl.tesseract.lib.persistantcontainer.NamedspacedKeyProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.persistence.PersistentDataType
import java.util.*

class EquipmentService(
    private val repository: EquipmentRepository,
    private val namespacedKeyProvider: NamedspacedKeyProvider,
    private val eventService: EventService,
) {

    fun getEquipment(playerUUID: UUID): Equipment {
        return repository.getById(playerUUID) ?: Equipment(playerUUID)
    }

    fun saveEquipment(equipment: Equipment) {
        repository.save(equipment)
    }

    fun <T> editEquipment(playerUUID: UUID, editFunction: (Equipment) -> T): T {
        val equipment = getEquipment(playerUUID)
        val result = editFunction(equipment)
        saveEquipment(equipment)
        return result
    }

    fun uninvokeAll(player: Player) {
        editEquipment(player.uniqueId) { equipment ->
            equipment.getInvoked().forEach { doUninvoke(player, it) }
        }
    }

    private fun doUninvoke(player: Player, invocable: Invocable) {
        val inv: PlayerInventory = player.inventory

        // Off hand
        if (invocable.slotType == EquipmentSlot.HAND && invocable.handSlot == -1)
            inv.setItem(EquipmentSlot.OFF_HAND, null)
        else if (isInvocable(inv.getItem(invocable.slotType)))
            inv.setItem(invocable.slotType, null)


        // Remove in any content slot
        for (i in inv.contents.indices) {
            val item = inv.contents[i]
            if (getInvocableName(item) == invocable.uniqueName)
                inv.setItem(i, null)
        }

        invocable.invoked = false
//        if (invocable.excludeOthers)
//            invocable.equipment.set(invocable.slotType, null)
        invocable.handSlot = -1

        invocable.onUninvoke(player, true)
    }

    fun uninvoke(player: Player, slot: EquipmentSlot) {
        editEquipment(player.uniqueId) { equipment ->
            equipment.get(slot)?.let { doUninvoke(player, it) }
        }
    }

    fun invoke(player: Player, type: Class<Invocable>, index: Int? = null, manualInvocation: Boolean = false): Boolean {
        return editEquipment(player.uniqueId) { equipment ->
            val invocable = equipment.get(type) ?: return@editEquipment false
            return@editEquipment if (index != null)
                doInvoke(player, invocable, index, manualInvocation)
            else
                doInvoke(player, invocable, manualInvocation)
        }
    }

    /**
     * Invoke the mainHand invocable at the given index
     * @param index index of the slot in the action bar. [[0,8]]
     */
    private fun doInvoke(player: Player, invocable: Invocable, index: Int, manualInvocation: Boolean = false): Boolean {
        require(index in (0..8)) { "Index must be between 0 and 8" }
        require(invocable.slotType == EquipmentSlot.HAND) { "Slot must be of type HAND" }

        if (!eventService.callEvent(PlayerInvocableInvokeEvent(player, invocable, manualInvocation)))
            return false

        val inv: PlayerInventory = player.inventory
        if (invocable.invoked)
            this.doUninvoke(player, invocable)
        // Remove any present invokable
        if (invocable.excludeOthers)
            this.uninvoke(player, invocable.slotType)

        val existingItem = inv.getItem(index)
        if (existingItem != null) {
            // If the present item is an invocable, uninvoke it
            asInvocable(player, existingItem)?.let { doUninvoke(player, it) }

            // If there is no slot to move existing item, cancel
            if (inv.firstEmpty() == -1) return false

            // Move the item, and put the invokable
            inv.setItem(index, invocable.getItem())
            inv.addItem(existingItem)
        } else {
            inv.setItem(index, invocable.getItem())
        }

        invocable.invoked = true
//        if (invocable.excludesOther()) invocable.equipment.set(EquipmentSlot.HAND, invocable)
        invocable.handSlot = index
        invocable.onInvoke(player, manualInvocation)
        return true
    }

    private fun doInvoke(player: Player, invocable: Invocable, manualInvocation: Boolean = true): Boolean {
        if (invocable.slotType == EquipmentSlot.HAND)
            return doInvoke(player, invocable, player.inventory.heldItemSlot, manualInvocation)
        if (!eventService.callEvent(PlayerInvocableInvokeEvent(player, invocable, manualInvocation)))
            return false
        val inv = player.inventory

        // Remove any present invokable
        uninvoke(player, invocable.slotType)

        // If there is already an item at that spot
        val other: ItemStack = inv.getItem(invocable.slotType)
        if (other != null) { // FIXME double check non nullability, I don't trust it
            // If there is no slot to move invocable item, cancel
            if (inv.firstEmpty() == -1) return false
            // Move the item, and put the invokable
            inv.setItem(invocable.slotType, invocable.getItem())
            inv.addItem(other)
        } else {
            inv.setItem(invocable.slotType, invocable.getItem())
        }
        invocable.invoked = true
        invocable.onInvoke(player, manualInvocation)
        return true
    }

    fun remove(playerUUID: UUID, type: Class<Invocable>) {
        editEquipment(playerUUID) { equipment ->
            val invocable = equipment.get(type) ?: return@editEquipment
            Bukkit.getPlayer(playerUUID)?.let { doUninvoke(it, invocable) }
            equipment.invocables.remove(invocable)
        }
    }

    fun add(playerUUID: UUID, invocable: Invocable) {
        editEquipment(playerUUID) { equipment ->
            equipment.invocables.add(invocable)
        }
    }

    fun isInvocable(item: ItemStack?): Boolean {
        return getInvocableName(item) != null
    }

    fun getInvocableName(item: ItemStack?): String? {
        if (item == null) return null
        return item.itemMeta.persistentDataContainer.get(
            namespacedKeyProvider.get("invocable_name"),
            PersistentDataType.STRING
        )
    }

    fun asInvocable(player: Player, item: ItemStack): Invocable? {
        return getInvocableName(item)?.let { name ->
            getEquipment(player.uniqueId).invocables.find { it.uniqueName == name }
        }
    }
}