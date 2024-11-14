package onl.tesseract.lib.util.menu;

import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InventoryClickOrDragEvent {

    InventoryInteractEvent getEvent();

    default void setCancelled(boolean cancelled)
    {
        getEvent().setCancelled(cancelled);
    }

    @NotNull
    Inventory getInventory();

    @Nullable
    Inventory getClickedInventory();

    @NotNull InventoryAction getAction();

    int getSlot();

    @Nullable
    ItemStack getCursor();

    boolean isRightClick();

    boolean isLeftClick();

    boolean isShiftClick();

    static InventoryClickOrDragEvent of(InventoryClickEvent event)
    {
        return new Click(event);
    }

    static InventoryClickOrDragEvent of(InventoryDragEvent event)
    {
        return new Drag(event);
    }
}

class Click implements InventoryClickOrDragEvent {
    private final InventoryClickEvent event;

    Click(final InventoryClickEvent event)
    {
        this.event = event;
    }

    @Override
    public InventoryClickEvent getEvent()
    {
        return event;
    }

    @Override
    public @NotNull Inventory getInventory()
    {
        return event.getInventory();
    }

    @Override
    @Nullable
    public ItemStack getCursor()
    {
        return event.getCursor();
    }

    @Override
    public boolean isRightClick()
    {
        return event.isRightClick();
    }

    @Override
    public boolean isLeftClick()
    {
        return event.isLeftClick();
    }

    @Override
    public boolean isShiftClick()
    {
        return event.isShiftClick();
    }

    @Override
    @Nullable
    public Inventory getClickedInventory()
    {
        return event.getClickedInventory();
    }

    @Override
    public int getSlot()
    {
        return event.getSlot();
    }

    @Override
    @NotNull
    public InventoryAction getAction()
    {
        return event.getAction();
    }

    public @NotNull ClickType getClick()
    {
        return event.getClick();
    }
}

class Drag implements InventoryClickOrDragEvent {
    private final InventoryDragEvent event;

    Drag(final InventoryDragEvent event)
    {
        this.event = event;
    }

    @Override
    public InventoryDragEvent getEvent()
    {
        return event;
    }

    @Override
    public @NotNull Inventory getInventory()
    {
        return event.getInventory();
    }

    @Override
    public @Nullable Inventory getClickedInventory()
    {
        return event.getInventory();
    }

    @Override
    public @NotNull InventoryAction getAction()
    {
        return event.getType() == DragType.SINGLE ? InventoryAction.PLACE_ONE : InventoryAction.PLACE_ALL;
    }

    @Override
    public int getSlot()
    {
        for (final Integer slot : event.getInventorySlots())
            return slot;
        throw new IllegalStateException();
    }

    @Override
    public @Nullable ItemStack getCursor()
    {
        return event.getOldCursor();
    }

    @Override
    public boolean isRightClick()
    {
        return event.getType() == DragType.SINGLE;
    }

    @Override
    public boolean isLeftClick()
    {
        return event.getType() == DragType.EVEN;
    }

    @Override
    public boolean isShiftClick()
    {
        return false;
    }
}