package onl.tesseract.tesseractlib.util.menu;

import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncButton extends AButton {

    private final Supplier<ItemStack> itemSupplier;

    public AsyncButton(final Supplier<ItemStack> itemSupplier, final Consumer<InventoryClickEvent> function)
    {
        super(function);
        this.itemSupplier = itemSupplier;
    }

    @Override
    public void onClick(final InventoryClickEvent event)
    {
        if (function != null)
            function.accept(event);
    }

    @Override
    protected void refreshItem()
    {
        new BukkitRunnable() {
            @Override
            public void run()
            {
                ItemStack item = itemSupplier.get();
                menu.setItem(index, item);
            }
        }.runTaskAsynchronously(TesseractLib.instance);
    }
}
