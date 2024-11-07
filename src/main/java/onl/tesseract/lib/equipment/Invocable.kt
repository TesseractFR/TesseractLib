package onl.tesseract.lib.equipment

import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.lib.persistantcontainer.NamedspacedKeyProvider
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.tesseractlib.util.ItemLoreBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

abstract class Invocable(
    val playerUUID: UUID,
    var invoked: Boolean,
    var slotType: EquipmentSlot,
    var handSlot: Int,
) {
    var item: ItemStack? = null
        private set

    abstract val uniqueName: String
    abstract val excludeOthers: Boolean

    abstract fun onUninvoke(player: Player, manuelRemoval: Boolean)
    abstract fun onInvoke(player: Player, manuelInvocation: Boolean)

    abstract fun createItem(): ItemStack
    open fun getInvocationPower(): Int = 0

    fun getItem(): ItemStack {
        item?.let { return it }
        return updateItem(false)
    }

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
        this.item = item
        if (updateInInventory) updateItemInInventory()
        return item
    }

    fun updateItemInInventory() {
        if (!invoked) return
        val player = Bukkit.getPlayer(playerUUID) ?: return
        val inv = player.inventory
        if (this.slotType != EquipmentSlot.HAND)
            inv.setItem(slotType, item)
        else if (handSlot != -1)
            inv.setItem(handSlot, item)
        else
            inv.setItem(EquipmentSlot.OFF_HAND, item)
    }
}