package onl.tesseract.lib.event

import org.bukkit.event.Event
import org.bukkit.plugin.Plugin

class EventService(private val plugin: Plugin) {

    /**
     * @return False if the event was cancelled
     */
    fun callEvent(event: Event) : Boolean {
        return event.callEvent()
    }
}