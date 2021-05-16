package onl.tesseract.tesseractlib.event;

import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.event.Cancellable;

public class PlayerInvocableInvokeEvent extends PlayerInvocableEvent implements Cancellable {
    private final boolean manualInvocation;
    private boolean cancelled;

    public PlayerInvocableInvokeEvent(TPlayer player, Invocable invocable, boolean manualInvocation)
    {
        super(player, invocable);
        this.manualInvocation = manualInvocation;
    }

    public boolean isManualInvocation()
    {
        return manualInvocation;
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
