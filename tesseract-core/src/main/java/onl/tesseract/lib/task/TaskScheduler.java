package onl.tesseract.lib.task;

import onl.tesseract.lib.Tick;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class TaskScheduler {

    private final Plugin plugin;

    public TaskScheduler(Plugin plugin){
        this.plugin = plugin;
    }

    public Plugin getPlugin(){
        return plugin;
    }

    public BukkitTask runLater(Runnable function) {
        return runLater(new Tick(0), function);
    }

    public BukkitTask runLater(Tick delay, Runnable function){
        if (delay.value() == 0L)
            return plugin.getServer().getScheduler().runTask(plugin,function);
        return plugin.getServer().getScheduler().runTaskLater(plugin,function, delay.value());
    }

    public void runAsync(Runnable function){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,function);
    }

    public void runAsyncTimer(Long delay,Long period,Consumer<BukkitTask> function){
        if(delay == 0L && period == 0L){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin,function);
        }
        else if(period == 0L){
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin,function,delay);
        }
        else{
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,function,delay,period);
        }
    }

    public BukkitTask runTimer(Long delay, Long period, Runnable function) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, function, delay, period);
    }

    public BukkitTask runTimer(Long delay,Long period, Long duration, Consumer<BukkitTask> function){
        AtomicReference<BukkitTask> task = new AtomicReference<>();
        AtomicInteger counter = new AtomicInteger();
        if(delay == 0 && period == 0){
            task.set(plugin.getServer().getScheduler().runTask(plugin,()->function.accept(task.get())));
        }
        else if(period == 0L){
            task.set(plugin.getServer().getScheduler().runTaskLater(plugin,()-> function.accept(task.get()),delay));
        }
        else {
            task.set(plugin.getServer().getScheduler().runTaskTimer(plugin,()->{
                if(duration > 0 && counter.getAndIncrement() > duration){
                    task.get().cancel();
                }
                else{
                 function.accept(task.get());
                }
            },delay,period));
        }
        return task.get();
    }
}