package onl.tesseract.lib.event.inventory;

import onl.tesseract.lib.inventory.InventoryInstanceConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class InventorySwitchEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final String from;
    private InventoryInstanceConfiguration to;
    private boolean cancel;

    public InventorySwitchEvent(@NotNull final Player who, final String from, final InventoryInstanceConfiguration to)
    {
        super(who);
        this.from = from;
        this.to = to;
    }

    @Override
    @NotNull
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    public String getFrom()
    {
        return from;
    }

    public InventoryInstanceConfiguration getTo()
    {
        return to;
    }

    public void setTo(final InventoryInstanceConfiguration to)
    {
        this.to = to;
    }

    @Override
    public boolean isCancelled()
    {
        return cancel;
    }

    @Override
    public void setCancelled(final boolean cancel)
    {
        this.cancel = cancel;
    }
}
