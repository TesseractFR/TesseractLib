package onl.tesseract.tesseractlib.event;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ColoredChat implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event)
    {
        if (event.isCancelled()) return;
        if (event.getPlayer().hasPermission("tesseract.chat.color") && event.message() instanceof TextComponent)
            event.message(colorMessage((TextComponent) event.message()));
    }

    static public TextComponent colorMessage(TextComponent message)
    {
        var coloredString = message.content().replaceAll("(&)([0-9a-fklnorm])", "§$2");
        return message.content(coloredString);
    }
}