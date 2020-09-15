package onl.tesseract.tesseractlib.event;

import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatDing implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event)
    {
        if (event.isCancelled()) return;
        ding(event.getMessage());
    }

    /**
     * Play a sound to each online players who are mentioned in a message
     * @param message Message sent.
     */
    static public void ding(String message)
    {
        Bukkit.getOnlinePlayers().stream().filter(p -> message.contains(p.getName())).forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1));
    }

    /**
     * Play a sound to players who are mentioned in a message
     * @param message Message sent
     * @param targets Potential targets. A sound will be played to each online target
     */
    static public void ding(String message, List<TPlayer> targets)
    {
        targets.stream().filter(p -> p.isOnline() && message.contains(p.getBukkitPlayer().getName()))
                .forEach(player -> player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1));
    }
}
