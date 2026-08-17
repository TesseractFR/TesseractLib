package onl.tesseract.lib.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A menu button whose item is loaded asynchronously.
 */
public class AsyncButton extends AButton {

    private final Supplier<ItemStack> itemSupplier;
    private final Plugin plugin;

    public AsyncButton(Supplier<ItemStack> itemSupplier, Plugin plugin, Consumer<InventoryClickEvent> function, boolean replace) {
        super(function, replace);
        this.itemSupplier = itemSupplier;
        this.plugin = plugin;
    }

    public AsyncButton(Supplier<ItemStack> itemSupplier, Plugin plugin, Consumer<InventoryClickEvent> function) {
        this(itemSupplier, plugin, function, false);
    }

    public AsyncButton(Supplier<ItemStack> itemSupplier, Plugin plugin) {
        this(itemSupplier, plugin, null, false);
    }

    public Supplier<ItemStack> getItemSupplier() {
        return itemSupplier;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Consumer<InventoryClickEvent> function = getFunction();
        if (function != null) {
            function.accept(event);
        }
    }

    @Override
    protected void refreshItem() {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack item = itemSupplier.get();
                InventoryView view = getMenu().getView();
                if (view == null) return;
                if (getSide() == Side.Top) {
                    view.getTopInventory().setItem(getIndex(), item);
                } else {
                    view.getBottomInventory().setItem(getIndex(), item);
                }
            }
        }.runTaskAsynchronously(getPlugin());
    }
}
