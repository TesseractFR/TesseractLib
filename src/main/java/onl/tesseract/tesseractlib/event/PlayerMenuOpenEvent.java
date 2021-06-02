package onl.tesseract.tesseractlib.event;

import onl.tesseract.tesseractlib.util.InventoryMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PlayerMenuOpenEvent extends Event implements Cancellable {
    static private final HandlerList handlerList = new HandlerList();

    InventoryMenu menu;
    Player player;
    private boolean cancelled;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public PlayerMenuOpenEvent(InventoryMenu menu, Player player)
    {
        this.menu = menu;
        this.player = player;
    }

    public InventoryMenu getMenu()
    {
        return menu;
    }

    public void setMenu(InventoryMenu menu)
    {
        this.menu = menu;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel)
    {
        cancelled = cancel;
    }
}
