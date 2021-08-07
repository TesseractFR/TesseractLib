package onl.tesseract.tesseractlib.event;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class ChatDing implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event)
    {
        if (event.isCancelled() || !(event.message() instanceof TextComponent))
            return;
        ding((TextComponent) event.message());
    }

    /**
     * Play a sound to each online players who are mentioned in a message
     * @param message Message sent.
     */
    static public void ding(TextComponent message)
    {
        Bukkit.getOnlinePlayers()
              .stream()
              .filter(p -> message.content().contains(p.getName()))
              .forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1));
    }

    /**
     * Play a sound to players who are mentioned in a message
     * @param message Message sent
     * @param targets Potential targets. A sound will be played to each online target
     */
    static public void ding(TextComponent message, List<TPlayer> targets)
    {
        targets.stream()
               .filter(p -> p.isOnline() && message.content().contains(p.getBukkitPlayer().getName()))
               .forEach(player -> player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1));
    }
}
