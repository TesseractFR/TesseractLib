package onl.tesseract.lib.task

import onl.tesseract.lib.Tick
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class TaskScheduler(private val plugin: Plugin) {

    fun runLater(delay: Tick = 0, function: () -> Unit): BukkitTask {
        return if (delay == 0L)
            plugin.server.scheduler.runTask(plugin, function)
        else
            plugin.server.scheduler.runTaskLater(plugin, function, delay)
    }

    fun runAsync(function: () -> Unit) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, function)
    }
}