package onl.tesseract.lib.event.equipment

import onl.tesseract.lib.equipment.Invocable
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerInvocableInvokeEvent(
    val player: Player,
    val invocable: Invocable,
    val isManualInvocation: Boolean
) : Event(), Cancellable {

    private var cancelled = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
