package onl.tesseract.lib.menu.event

import onl.tesseract.lib.menu.Menu
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerMenuOpenEvent(
    val menu: Menu,
    val viewer: Player,
) : Event(), Cancellable {

    private var cancelled = false

    override fun getHandlers(): HandlerList = handlerList

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    companion object {
        @JvmField
        val handlerList = HandlerList()
    }
}