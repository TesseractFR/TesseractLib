package onl.tesseract.lib.menu

import org.bukkit.event.inventory.InventoryClickEvent
import java.util.function.Consumer

abstract class AButton(
    val function: Consumer<InventoryClickEvent>?,
    val replace: Boolean = false,
) {

    protected lateinit var menu: Menu
    protected lateinit var side: Side
    protected var index: Int = 0

    abstract fun onClick(event: InventoryClickEvent)

    fun draw(menu: Menu, index: Int, side: Side = Side.Top) {
        this.menu = menu
        this.index = index
        this.side = side
        refreshItem()
    }

    protected abstract fun refreshItem()

    enum class Side { Top, Bottom }
}
