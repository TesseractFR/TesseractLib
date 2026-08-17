package onl.tesseract.lib.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A standard menu button with a static item stack.
 */
public class Button extends AButton {

    protected ItemStack item;
    private final Function<ItemStack, ItemStack> onPlace;

    public Button(ItemStack item, Consumer<InventoryClickEvent> function, boolean replace, Function<ItemStack, ItemStack> onPlace) {
        super(function, replace);
        this.item = item;
        this.onPlace = onPlace;
    }

    public Button(ItemStack item, Consumer<InventoryClickEvent> function, boolean replace) {
        this(item, function, replace, null);
    }

    public Button(ItemStack item, Consumer<InventoryClickEvent> function) {
        this(item, function, false, null);
    }

    public Button(ItemStack item) {
        this(item, null, false, null);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (this.onPlace != null && event.getCursor().getType() != Material.AIR) {
            ItemStack res = this.onPlace.apply(event.getCursor());
            if (isReplace()) {
                this.item = res;
                refreshItem();//TODO need to refresh ?
            }
        } else {
            Consumer<InventoryClickEvent> function = getFunction();
            if (function != null) {
                function.accept(event);
            }
        }
    }

    @Override
    protected void refreshItem() {
        InventoryView view = getMenu().getView();
        if (view == null) return;
        if (getSide() == Side.Top) {
            view.getTopInventory().setItem(getIndex(), item);
        } else {
            view.getBottomInventory().setItem(getIndex(), item);
        }
    }
}
