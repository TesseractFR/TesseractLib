package onl.tesseract.tesseractlib.util.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class Button extends AButton {
    private ItemStack itemStack;
    private final Function<ItemStack, ItemStack> onPlace;

    public Button(@NotNull final ItemStack itemStack, final Consumer<InventoryClickEvent> function)
    {
        super(function);
        this.itemStack = itemStack;
        this.onPlace = null;
    }

    public Button(@NotNull final ItemStack itemStack, final Function<ItemStack, ItemStack> function, boolean replace)
    {
        super(null);
        this.itemStack = itemStack;
        this.onPlace = function;
        this.replace = replace;
    }

    public Button(@NotNull final ItemStack itemStack)
    {
        super(null);
        this.itemStack = itemStack;
        this.onPlace = null;
    }

    @NotNull
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public void onClick(final InventoryClickEvent event)
    {
        if (this.onPlace != null && event.getCursor() != null && event.getCursor().getType() != Material.AIR)
        {
            var res = this.onPlace.apply(event.getCursor());
            if (replace)
                this.itemStack = res;
        }
        else if (function != null)
            function.accept(event);
    }

    @Override
    public void draw(final InventoryMenu menu, final int index)
    {
        menu.setItem(index, getItemStack());
    }

    @Override
    protected void refreshItem()
    {
        menu.setItem(index, getItemStack());
    }
}

