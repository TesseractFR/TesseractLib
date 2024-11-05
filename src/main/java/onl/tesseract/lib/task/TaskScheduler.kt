package onl.tesseract.lib.task

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler

typealias Tick = Long

class TaskScheduler(private val plugin: Plugin, private val scheduler: BukkitScheduler) {

    fun runLater(delay: Tick = 0, function: () -> Unit) {
        if (delay == 0L)
            scheduler.runTask(plugin, function)
        else
            scheduler.runTaskLater(plugin, function, delay)
    }

    fun runAsync(function: () -> Unit) {
        scheduler.runTaskAsynchronously(plugin, function)
    }
}