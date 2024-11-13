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

    fun runAsyncTimer(plugin: Plugin, delay: Long = 0, period: Long = 0, function: (BukkitTask) -> Unit) {
        if (delay == 0L && period == 0L)
            plugin.server.scheduler.runTaskAsynchronously(plugin, function)
        else if (period == 0L)
            plugin.server.scheduler.runTaskLaterAsynchronously(plugin, function, delay)
        else
            plugin.server.scheduler.runTaskTimerAsynchronously(plugin, function, delay, period)
    }
}