package onl.tesseract.lib.service

import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class PluginService(private val plugin: Plugin) {

    fun registerEventListener(listener: Listener) {
        plugin.server.pluginManager.registerEvents(listener, plugin)
    }

    fun unregisterEventListener(listener: Listener) {
        HandlerList.unregisterAll(listener)
    }
}