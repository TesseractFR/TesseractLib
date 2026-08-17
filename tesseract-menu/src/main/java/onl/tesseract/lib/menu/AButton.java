package onl.tesseract.lib.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.function.Consumer;

/**
 * Abstract base class for menu buttons.
 */
public abstract class AButton {

    private final Consumer<InventoryClickEvent> function;
    private final boolean replace;

    protected Menu menu;
    protected Side side;
    protected int index = 0;

    public AButton(Consumer<InventoryClickEvent> function, boolean replace) {
        this.function = function;
        this.replace = replace;
    }

    public Consumer<InventoryClickEvent> getFunction() {
        return function;
    }

    public boolean isReplace() {
        return replace;
    }

    public abstract void onClick(InventoryClickEvent event);

    public final void draw(Menu menu, int index, Side side) {
        this.menu = menu;
        this.index = index;
        this.side = side;
        refreshItem();
    }

    protected abstract void refreshItem();

    public Menu getMenu() {
        return menu;
    }

    public Side getSide() {
        return side;
    }

    public int getIndex() {
        return index;
    }

    public void draw(Menu menu, Integer key) {
        draw(menu, key,Side.Top);
    }

    /**
     * Button position relative to the menu.
     */
    public enum Side {
        Top,
        Bottom
    }
}
