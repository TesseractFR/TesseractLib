package onl.tesseract.tesseractlib.chat.tag;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.regex.Pattern;

public class TagEventHandler implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMessage(AsyncChatEvent event)
    {
        if (event.message() instanceof TextComponent text)
        {
            text = insertPlayerTags(text);
            var player = TPlayer.get(event.getPlayer());
            event.message(Tag.applyAll(text, player));
        }
    }

    public static TextComponent insertPlayerTags(final TextComponent text)
    {
        return insertPlayerTags(text, Bukkit.getOnlinePlayers());
    }

    public static TextComponent insertPlayerTags(final TextComponent text, Collection<? extends Player> targets)
    {
        for (var player : targets)
        {
            var matcher = Pattern.compile(".*(" + player.getName() + ").*")
                                 .matcher(text.content());
            if (matcher.matches())
            {
                int index = matcher.toMatchResult().start(1);
                return text.content(text.content().substring(0, index) + "@" + text.content().substring(index));
            }
        }
        return text;
    }
}
