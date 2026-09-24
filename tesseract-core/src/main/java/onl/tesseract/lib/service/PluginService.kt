package onl.tesseract.lib.service

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

class PluginService(private val plugin: Plugin) {

    fun getPlugin(): Plugin {
        return plugin
    }

    fun registerEventListener(listener: Listener) {
        plugin.server.pluginManager.registerEvents(listener, plugin)
    }

    fun unregisterEventListener(listener: Listener) {
        HandlerList.unregisterAll(listener)
    }

    fun createInventory(size: Int, title: Component): Inventory {
        return Bukkit.createInventory(null, size, title)
    }

    fun createInventory(type: InventoryType, title: Component): Inventory {
        return Bukkit.createInventory(null, type, title)
    }
}