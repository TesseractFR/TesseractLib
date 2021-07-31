package onl.tesseract.tesseractlib.event;

import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerInvocableEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    TPlayer player;
    Invocable invocable;

    public PlayerInvocableEvent(TPlayer player, Invocable invocable)
    {
        this.player = player;
        this.invocable = invocable;
    }

    public TPlayer getPlayer()
    {
        return player;
    }

    public Invocable getInvocable()
    {
        return invocable;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    @Override
    @NotNull
    public HandlerList getHandlers()
    {
        return handlerList;
    }
}
