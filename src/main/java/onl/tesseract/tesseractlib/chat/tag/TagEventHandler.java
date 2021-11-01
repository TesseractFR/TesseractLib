package onl.tesseract.tesseractlib.chat.tag;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

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

    public static TextComponent insertPlayerTags(TextComponent text, Iterable<? extends Player> targets)
    {
        for (var player : targets)
        {
            var pattern = Pattern.compile("(.* )?(" + player.getName() + ")( .*)?");
            var matcher = pattern.matcher(text.content());
            while (matcher.matches())
            {
                int index = matcher.toMatchResult().start(2);
                text = text.content(text.content().substring(0, index) + "@" + text.content().substring(index));
                matcher = pattern.matcher(text.content());
            }
        }
        return text;
    }
}
