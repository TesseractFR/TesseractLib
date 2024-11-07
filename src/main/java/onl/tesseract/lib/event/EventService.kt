package onl.tesseract.lib.event

import org.bukkit.event.Event
import org.bukkit.plugin.Plugin

class EventService(private val plugin: Plugin) {

    fun callEvent(event: Event) : Boolean {
        return event.callEvent()
    }
}