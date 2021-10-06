package onl.tesseract.tesseractlib.util.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class AButton {
    protected Consumer<InventoryClickEvent> function;
    protected InventoryMenu menu;
    protected int index;
    protected boolean replace;

    public AButton(final Consumer<InventoryClickEvent> function)
    {
        this.function = function;
    }

    @Nullable
    public Consumer<InventoryClickEvent> getFunction()
    {
        return function;
    }

    public abstract void onClick(final InventoryClickEvent event);

    public void draw(final InventoryMenu menu, final int index)
    {
        this.menu = menu;
        this.index = index;
        refreshItem();
    }

    protected abstract void refreshItem();
}
