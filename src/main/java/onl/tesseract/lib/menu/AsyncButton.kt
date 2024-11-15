package onl.tesseract.lib.menu

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.function.Consumer
import java.util.function.Supplier

class AsyncButton(
    val itemSupplier: Supplier<ItemStack>,
    val plugin: Plugin,
    function: Consumer<InventoryClickEvent>? = null,
    replace: Boolean = false,
) : AButton(function, replace) {

    override fun onClick(event: InventoryClickEvent) {
        function?.accept(event)
    }

    override fun refreshItem() {
        object : BukkitRunnable() {
            override fun run() {
                val item = itemSupplier.get()
                menu.view?.topInventory?.setItem(index, item)
            }
        }.runTaskAsynchronously(plugin)
    }
}
