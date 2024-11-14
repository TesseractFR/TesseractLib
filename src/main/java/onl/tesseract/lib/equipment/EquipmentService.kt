package onl.tesseract.lib.equipment

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.lib.event.EventService
import onl.tesseract.lib.event.equipment.PlayerInvocableInvokeEvent
import onl.tesseract.lib.menu.Menu
import onl.tesseract.lib.persistantcontainer.NamedspacedKeyProvider
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.lib.task.TaskScheduler
import onl.tesseract.lib.util.ChatFormats
import org.bukkit.Bukkit
import org.bukkit.entity.Hanging
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.Plugin
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

        invocable.isInvoked = false
//        if (invocable.excludeOthers)
//            invocable.equipment.set(invocable.slotType, null)
        invocable.handSlot = -1

        invocable.onUninvoke(player, true)
    }

    fun uninvoke(player: Player, invocable: Invocable) {
        editEquipment(player.uniqueId) {
            doUninvoke(player, invocable)
        }
    }

    fun uninvoke(player: Player, slot: EquipmentSlot) {
        editEquipment(player.uniqueId) { equipment ->
            equipment.get(slot)?.let { doUninvoke(player, it) }
        }
    }

    fun <T : Invocable> invoke(player: Player, type: Class<T>, index: Int? = null, manualInvocation: Boolean = false): Boolean {
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
        if (invocable.isInvoked)
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

        invocable.isInvoked = true
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
        invocable.isInvoked = true
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
        return namespacedKeyProvider.getString(item.itemMeta, "invocable_name")
    }

    fun asInvocable(player: Player, item: ItemStack): Invocable? {
        return getInvocableName(item)?.let { name ->
            getEquipment(player.uniqueId).invocables.find { it.uniqueName == name }
        }
    }

    fun loadEquipment(player: Player) {
        getEquipment(player.uniqueId).getInvoked().forEach {
            ServiceContainer[TaskScheduler::class.java].runLater {
                if (it.slotType == EquipmentSlot.HAND) {
                    if (it.handSlot > -1)
                        player.inventory.setItem(it.handSlot, it.getItem())
                    else if (it.handSlot == -1)
                        player.inventory.setItem(EquipmentSlot.OFF_HAND, it.getItem())
                } else {
                    player.inventory.setItem(it.slotType, it.getItem())
                }
                it.onInvoke(player, false)
            }
        }
    }

    /**
     * Method to call at server start to register internal event handlers
     */
    fun registerEventHandler(plugin: Plugin) {
        val eventHandler = EquipmentEventHandler(this)
        plugin.server.pluginManager.registerEvents(eventHandler, plugin)
    }
}

class EquipmentEventHandler(private val service: EquipmentService) : Listener {
    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        // When the player dies, keep all invokable objects
        val iterator = event.drops.iterator()
        while (iterator.hasNext()) {
            val drop = iterator.next()
            if (service.isInvocable(drop)) {
                // Remove from dropped items
                iterator.remove()
                event.itemsToKeep.add(drop)
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        val player = event.whoClicked as Player
        // Cancel the event if the player move the item with hotbar buttons
        if (event.click == ClickType.NUMBER_KEY) {
            if (service.isInvocable(event.cursor) || service.isInvocable(player.inventory.getItem(event.hotbarButton))) {
                event.isCancelled = true
                return
            }
        }
        // Cancel the event if the player move the item with off hand swap button
        if (event.click == ClickType.SWAP_OFFHAND) {
            if (service.isInvocable(event.cursor) || service.isInvocable(player.inventory.itemInOffHand)) {
                event.isCancelled = true
                return
            }
        }

        val item = event.currentItem ?: return
        // Check that it is a invokable item
        if (!service.isInvocable(item)) return
        event.isCancelled = true

        ServiceContainer[TaskScheduler::class.java].runLater(1) {
            val invocable = service.asInvocable(player, item) ?: return@runLater
            // If shift click, uninvoke it
            if (event.isShiftClick) {
                service.uninvoke(player, invocable)
                player.sendMessage(
                    ChatFormats.EQUIPMENT.append(Component.text("Équipement désinvoqué. Vous pouvez ré-invoquer un équipement via "))
                        .append(Component.text("/equipement", NamedTextColor.GOLD))
                )
            } else if (invocable.slotType == EquipmentSlot.HAND) {
                val subMenu: Menu? = invocable.subMenu()
                if (event.click == ClickType.RIGHT || subMenu == null) {
                    val menu = EquipmentMenu(player, service)
                    menu.mainHandInvocationMenu(invocable, player)
                } else {
                    subMenu.open(player)
                }
            } else {
                invocable.useInInventory(event)
            }
        }
    }

    @EventHandler
    fun onUse(event: PlayerInteractEvent) {
        if (!event.hasItem()) return
        val item = checkNotNull(event.item)
        val invocable = service.asInvocable(event.player, item) ?: return
        invocable.use(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun hanging(event: PlayerInteractEntityEvent) {
        if (event.rightClicked is Hanging) {
            if (service.isInvocable(event.player.inventory.getItem(event.hand)))
                event.isCancelled = true
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack
        // Check that it is a invokable item
        if (service.isInvocable(item))
            event.isCancelled = true
    }

    @EventHandler
    fun onSwap(event: PlayerSwapHandItemsEvent) {
        val main = event.mainHandItem
        val off = event.offHandItem
        if (service.isInvocable(main))
            onSwapHandler(event, main, -1)
        if (service.isInvocable(off))
            onSwapHandler(event, off, event.player.inventory.heldItemSlot)
    }

    /**
     * Change the known hand slot of the invocable when swapped to/from offhand, or cancel the event if the invocable
     * excludes others
     */
    private fun onSwapHandler(event: PlayerSwapHandItemsEvent, invocableItem: ItemStack, destinationSlot: Int) {
        val invocable = service.asInvocable(event.player, invocableItem)
        if (invocable != null && invocable.excludeOthers) {
            event.isCancelled = true
            return
        } else if (invocable != null) {
            invocable.handSlot = destinationSlot
        }
    }

    /**
     * Uninvoke invocables when player leaves
     */
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        // Remove the invocable when the player leaves
        val equipment = service.getEquipment(event.player.uniqueId)
        val inventory = event.player.inventory
        equipment.getInvoked().forEach { invocable ->
            if (invocable.slotType == EquipmentSlot.HAND && invocable.handSlot >= 0)
                inventory.clear(invocable.handSlot)
            else if (invocable.slotType == EquipmentSlot.HAND && invocable.handSlot == -1)
                inventory.setItem(EquipmentSlot.OFF_HAND, null)
            else
                inventory.setItem(invocable.slotType, null)
            invocable.onUninvoke(event.player, false)
        }
    }
}