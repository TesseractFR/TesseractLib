package onl.tesseract.tesseractlib.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ColoredChat implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event)
    {
        if (event.isCancelled()) return;
        if (event.getPlayer().hasPermission("tesseract.chat.color"))
            event.setMessage(colorMessage(event.getMessage()));
    }

    static public String colorMessage(String message)
    {
        return message.replaceAll("(&)([0-9a-fklnorm])", "§$2");
    }
}