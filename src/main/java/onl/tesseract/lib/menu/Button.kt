package onl.tesseract.lib.menu

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class Button @JvmOverloads constructor(
    var item: ItemStack,
    function: Consumer<InventoryClickEvent>? = null,
    replace: Boolean = false,
    val onPlace: ((ItemStack) -> ItemStack)? = null,
) : AButton(function, replace) {

    override fun onClick(event: InventoryClickEvent) {
        if (this.onPlace != null && event.cursor.type != Material.AIR) {
            val res = this.onPlace.invoke(event.cursor)
            if (replace) {
                this.item = res
//                refreshItem() TODO need to refresh ?
            }
        } else
            function?.accept(event)
    }

    override fun refreshItem() {
        menu.view?.topInventory?.setItem(index, item)
    }
}

