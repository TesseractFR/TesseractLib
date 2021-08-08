package onl.tesseract.tesseractlib.util.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Button {
    private final ItemStack itemStack;
    private final Consumer<InventoryClickEvent> function;

    public Button(@NotNull final ItemStack itemStack, final Consumer<InventoryClickEvent> function)
    {
        this.itemStack = itemStack;
        this.function = function;
    }

    public Button(@NotNull final ItemStack itemStack)
    {
        this(itemStack, null);
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
        if (function != null)
            function.accept(event);
    }
}
