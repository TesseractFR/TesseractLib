package onl.tesseract.tesseractlib.util.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class Button implements IButton {
    private ItemStack itemStack;
    private final Consumer<InventoryClickEvent> function;
    private final Function<ItemStack, ItemStack> onPlace;
    private boolean replace;

    public Button(@NotNull final ItemStack itemStack, final Consumer<InventoryClickEvent> function)
    {
        this.itemStack = itemStack;
        this.function = function;
        this.onPlace = null;
    }

    public Button(@NotNull final ItemStack itemStack, final Function<ItemStack, ItemStack> function, boolean replace)
    {
        this.itemStack = itemStack;
        this.onPlace = function;
        this.replace = replace;
        this.function = null;
    }

    public Button(@NotNull final ItemStack itemStack)
    {
        this.itemStack = itemStack;
        this.function = null;
        this.onPlace = null;
    }

    @NotNull
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    @Nullable
    public Consumer<InventoryClickEvent> getFunction()
    {
        return function;
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
}

