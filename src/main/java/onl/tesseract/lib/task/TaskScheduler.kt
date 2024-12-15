package onl.tesseract.lib.task

import onl.tesseract.lib.Tick
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class TaskScheduler(val plugin: Plugin) {

    fun runLater(delay: Tick = 0, function: () -> Unit): BukkitTask {
        return if (delay == 0L)
            plugin.server.scheduler.runTask(plugin, function)
        else
            plugin.server.scheduler.runTaskLater(plugin, function, delay)
    }

    fun runAsync(function: () -> Unit) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, function)
    }

    fun runAsyncTimer(delay: Long = 0, period: Long = 0, function: (BukkitTask) -> Unit) {
        if (delay == 0L && period == 0L)
            plugin.server.scheduler.runTaskAsynchronously(plugin, function)
        else if (period == 0L)
            plugin.server.scheduler.runTaskLaterAsynchronously(plugin, function, delay)
        else
            plugin.server.scheduler.runTaskTimerAsynchronously(plugin, function, delay, period)
    }

    fun runTimer(delay: Long = 0, period: Long = 0, function: () -> Unit): BukkitTask {
        return plugin.server.scheduler.runTaskTimer(plugin, function, delay, period)
    }

    fun runTimer(delay: Long = 0, period: Long = 0, duration: Long = 0, function: (BukkitTask) -> Unit): BukkitTask {
        var task: BukkitTask? = null
        var counter = 0
        task = if (delay == 0L && period == 0L)
            plugin.server.scheduler.runTask(plugin, { ->
                function(task!!)
            })
        else if (period == 0L)
            plugin.server.scheduler.runTaskLater(plugin, { ->
                function(task!!)
            }, delay)
        else
            plugin.server.scheduler.runTaskTimer(plugin, { ->
                if (duration > 0 && counter++ > duration)
                    task?.cancel()
                else
                    function(task!!)
            }, delay, period)
        return task
    }
}