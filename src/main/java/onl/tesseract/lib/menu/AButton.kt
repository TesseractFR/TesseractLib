package onl.tesseract.lib.menu

import org.bukkit.event.inventory.InventoryClickEvent
import java.util.function.Consumer

abstract class AButton(
    val function: Consumer<InventoryClickEvent>?,
    val replace: Boolean = false,
) {

    protected lateinit var menu: Menu
    protected var index: Int = 0

    abstract fun onClick(event: InventoryClickEvent)

    fun draw(menu: Menu, index: Int) {
        this.menu = menu
        this.index = index
        refreshItem()
    }

    protected abstract fun refreshItem()
}
