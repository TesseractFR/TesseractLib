package onl.tesseract.lib.equipment

import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.lib.menu.Menu
import onl.tesseract.lib.persistantcontainer.NamedspacedKeyProvider
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.lib.util.ItemLoreBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

abstract class Invocable @JvmOverloads constructor(
    val playerUUID: UUID,
    open var isInvoked: Boolean = false,
    open var handSlot: Int = -1,
) {
    private var _item: ItemStack? = null

    abstract val slotType: EquipmentSlot
    open val uniqueName: String = this.javaClass.simpleName
    /**
     * Does this invocable excludes other invocables when invoked
     */
    open val excludeOthers: Boolean = true

    abstract fun onUninvoke(player: Player, manuelRemoval: Boolean)
    abstract fun onInvoke(player: Player, manuelInvocation: Boolean)
    /**
     * Called when the item is clicked in the inventory.
     * @param event Event of the interaction
     */
    abstract fun useInInventory(event: InventoryClickEvent)
    /**
     * Called when an offHand or mainHand invocable is used.
     * @param event Event of the interaction.
     */
    abstract fun use(event: PlayerInteractEvent)

    /**
     * Create the item shown in the player's inventory and invocation menu.
     *
     * @return Newly constructed item stack
     *
     * @see [Invocable.updateItem]
     * @see [Invocable.getItem]
     */
    protected abstract fun createItem(): ItemStack
    open fun getInvocationPower(): Int = 0
    open fun subMenu(): Menu? = null

    /**
     * Returns the itemStack this invocable represents. Constructs the item if no item is in cache
     *
     * @return Itemstack of this invokable
     */
    fun getItem(): ItemStack {
        _item?.let { return it }
        return updateItem(false)
    }

    open fun onItemHeld() {

    }

    open fun onItemUnHeld() {

    }

    /**
     * Update the item in cache by calling {@link Invocable#createItem()}
     *
     * @param updateInInventory If true, the player's inventory will be searched to replace the item if invoked
     */
    protected fun updateItem(updateInInventory: Boolean): ItemStack {
        val item = createItem()
        val meta = item.itemMeta
        meta.isUnbreakable = true
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
        val key = ServiceContainer[NamedspacedKeyProvider::class.java].get("invocable_name")
        meta.persistentDataContainer.set(key, PersistentDataType.STRING, uniqueName)
        item.setItemMeta(meta)
        if (getInvocationPower() > 0) {
            val lore = ItemLoreBuilder()
                .append(meta.lore() ?: emptyList())
                .newline(2)
                .append("Puissance d'invocation : ", NamedTextColor.GRAY)
                .append(getInvocationPower().toString() + "", NamedTextColor.YELLOW)
                .get()
            item.lore(lore)
        }
        this._item = item
        if (updateInInventory) updateItemInInventory()
        return item
    }

    /**
     * Updates the item instance of this invocable in the inventory, to match the new item model.
     */
    fun updateItemInInventory() {
        if (!isInvoked) return
        val player = Bukkit.getPlayer(playerUUID) ?: return
        val inv = player.inventory
        if (this.slotType != EquipmentSlot.HAND)
            inv.setItem(slotType, _item)
        else if (handSlot != -1)
            inv.setItem(handSlot, _item)
        else
            inv.setItem(EquipmentSlot.OFF_HAND, _item)
    }
}