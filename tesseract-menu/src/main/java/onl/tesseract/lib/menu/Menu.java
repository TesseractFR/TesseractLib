package onl.tesseract.lib.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.Tick;
import onl.tesseract.lib.itembuilder.ItemBuilder;
import onl.tesseract.lib.logger.LoggerFactory;
import onl.tesseract.lib.service.PluginService;
import onl.tesseract.lib.service.ServiceContainer;
import onl.tesseract.lib.task.TaskScheduler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Create in-game menus using immutable inventories. Buttons are symbolized by items in the top inventory, and trigger
 * a callback when clicked.
 */
public class Menu implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(Menu.class);

    // Button icons (loaded once as companion object)
    private static final ItemStack BACK_BUTTON;
    private static final ItemStack CLOSE_BUTTON;
    private static final ItemStack CHECK_MARK_BUTTON;

    static {
        BACK_BUTTON = new ItemBuilder(Material.PLAYER_HEAD)
                .customHead(
                        "eyJ0aW1lc3RhbXAiOjE1MzQ0NTU2Njg3MTgsInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mN2FhY2FkMTkzZTIyMjY5NzFlZDk1MzAyZGJhNDMzNDM4YmU0NjQ0ZmJhYjVlYmY4MTgwNTQwNjE2NjdmYmUyIn19fQ==",
                        "P1pFjz8nr9mcyMiBoisU0ON86W+7MG4K3ieuuLKrAvBwd11KFNrKqY7t0vp3kUVF0TNCaN/1oPEN27Ahl/L7l0yrM6c+tiPBQQkEGQiQpMqHPPM0bSVdT6m9Sv0zW7ZAytJXuRoK/JFr6InxMoAcd/lvhZvuNyL60nW7NRDtKYyac2/Z1X0Hk+aEI6XwuAE1g2SVkxyv7FWTrOWE+KO2Umv/w3GteV9fT6moHYOHhs0PmhqzrXHtqK+jfXB0b/eiVhQSBBiR4e9A8Svj+XJDzvH2csfZu9XeQ2kAUuJMQ09CpxvrBeQ1E8FFBFk8UAxQH/ANLMCcg+SsmJxnrR1SS45PP1BM+arm/VdmVsqzk60VBDyREhQmqtB+h6IDbYLOzIvggZhF3nQyolC/uklYy7SJ4WP5R3XuQtT/wPeS9s6BixtNhvbTVA7Yv02c8XTKMZpI4gN9sX2icbtOuYlIBf7w4aXNLBfi896RONuU4odS7X3mz7HwmNN2Zyu+XOPU8njTcbbIDxBWmTsfK/ROnFol19b4Vd8geyQSbFDZvsvqrLYS83mnBoQXODowHnSH8rRXAdQ0F8o/QkmUylz4tlSk5oi+y4Vv1EOKtut05HGyor38WFbO0niBYDv0EmHSO33m9vLYVJzoE65wXGT6bLhhrxdasBAr+WvkExtcgPg="
                )
                .build();
        CLOSE_BUTTON = new ItemBuilder(Material.PLAYER_HEAD)
                .customHead(
                        "eyJ0aW1lc3RhbXAiOjE1ODcxNTc4ODI2NDMsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZkMDU2YzdlZGRhNTc4Y2MyYTEwYzU1NTBjNTY2YTVmNzQxNmEzMzZkZmQwNjIzMTZkOGMwYjI4NTFjZGU5M2MifX19",
                        "bRUXqC6nIpHlgj3rWIW1STtWgK5BlDHcQq8PnZzXmmTjrLI5D9KzP9TKmq3v3PbtPIOvmoB+sIWIOTQp9gsPiqkWmtEh9cdyDe2ak7X5U+z79Got/p649Sd12W+Pg8iR0YVOJ7hOYRjFkYTi/f/2fi0V3YG+kIXDyNPkUXZHfEOdLnTRmUjbsT5EjM747+o3tX9bg7bX9i6Nsxo0/zIi4XPt3g8i0sBuo3ytgHb7WJQ/QxCtCoBg2M5SMr9pnk4bO9xtoLBmY/t2PcJqagMTY1QeWShtnKr4sHG7hXyXXf8hPFAnizv1zyMWsfuyPQCWqcVfHr3GDFj/Osxylov/faLOlWG2oCJCAODKVbZ6D+1nyPCgkO/ouK8WQ+U+Iyzzakx+zwaS7P22/9gw1PM8pfkF356bP6PWy1hHIb2EvYh1tVgkOCbUYIzdgkvGnts+qBAGA39Uqhz0lZ3IgW4uVSygbkcMqASGiwLfaWGsNsZF4Q24phOFIFiyO1oVw4eeV9GOPVYhKdT2RAePm4c4duoAing36vuwvV3oOUbJXUMHjMUPtXeLX5uJqJt2DetZCXiqIUyxqCXgCOLjJujjoyuyHpD/2m0IsPhg2a/dgfOcm/25K1lNaKOIgYhZ773eSyCZbkjN0h5bzZY8S87n3zTrHPqpGGUYzdRptZvvwAc="
                )
                .build();
        CHECK_MARK_BUTTON = new ItemBuilder(Material.PLAYER_HEAD)
                .customHead(
                        "ewogICJ0aW1lc3RhbXAiIDogMTU5MjY5NjU3ODgxMSwKICAicHJvZmlsZUlkIiA6ICI3MzgyZGRmYmU0ODU0NTVjODI1ZjkwMGY4OGZkMzJmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4cWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgyMzUwODA4NDg4YjQ0Y2EwNmVhNGE1NDUzY2QwZDM4N2I2MTg5YjVhZmRlY2EwYjhjNjJlNTVkNjg1MTBhMSIKICAgIH0KICB9Cn0=",
                        "Z98pJmCgeb9xFh9V37708rOQz2j2zrOH4bkj+Yu7xcHPUnX3TObOAxQnp9iJoWoMDaQ2z9T6aJw3d/WXqQK8Ektqk0v59DrOHlv6Ei17uDHKoLrIlNfzT+YYY/x/cyeEd9EktFYp8GsvCHJi8YtJHV2msH7Z7j0d4IhB/MbE/ploYJfrIG3x8haziBWMdTLbZhr/ykGPh+d2uiCVHgjxj5w2HSjvhyaW5a0PDsJ9uiiPI8ZZaOLZHELZXHOPliy0cJrNH+KH4yV22vOR8XAzooSHv384xUMjkkrzYwGFK1HFaLelNXfve9odQC0Rjv0eEqFjnobiVq4wzC0Pl1nOUv0+Q4J/sOLs1GAy4P26usz1IZzfLgFw/Sx7h5xRuvdD6/6P50xr3jja8o3gH35DGID//ImivYniDz1eOTTfreosABjxVn3cTsyXOlDx+F9YHlYgwxmT4gcJXpTxFN52Ef1H9Qw8Moq6t7xbVMaOv4aHlSsMug94/5rHJV1745HQ/pDRcVNSE5qb8FTNtHJjhOsmm6ZLwnyf/5JAjkP2yp7qP+9Be6O/3f38v0Yr7UzWgLwvyJeoat/oKSif/ub6ujOxNTBcIVsPRNONaN64Rr44qm3zXU+F40E0wgLAx98SKLZw/JssVsTvHat8WHFMaZ2vMXmyZeNXS4c+Q+kPjdA="
                )
                .build();
    }

    // Menu configuration
    protected final MenuSize size;
    protected final Component title;
    protected final Menu previous;
    private final boolean freezeBottom;
    private final InventoryType type;

    // Internal state
    private final Map<Integer, AButton> buttons = new HashMap<>();
    protected Player viewer;
    protected InventoryView view;

    // Primary constructor
    public Menu(
            MenuSize size,
            Component title,
            Menu previous,
            boolean freezeBottom,
            InventoryType type
    ) {
        this.size = size;
        this.title = title;
        this.previous = previous;
        this.freezeBottom = freezeBottom;
        this.type = type;
    }


    public Menu(MenuSize size, String title) {
        this(size, Component.text(title), null, true, null);
    }

    public Menu(MenuSize size, Component title,Menu previous) {
        this(size, title, previous, true, null);
    }
    public Menu(MenuSize size, Component title,Menu previous,boolean freezeBottom) {
        this(size, title, previous, freezeBottom, null);
    }

    public Menu(MenuSize size, String title, NamedTextColor color) {
        this(size, Component.text(title, color), null, true, null);
    }
    public Menu(MenuSize size, String title, Menu previous) {
        this(size, Component.text(title), previous, true, null);
    }

    public Menu( MenuSize size,  String title,  NamedTextColor color,  Menu previous) {
        this(size, Component.text(title,color),previous);
    }

    public static ItemStack getBackButton() {
        return BACK_BUTTON;
    }

    public static ItemStack getCloseButton() {
        return CLOSE_BUTTON;
    }

    public static ItemStack getCheckMarkButton() {
        return CHECK_MARK_BUTTON;
    }

    /**
     * Open the menu for the given player.
     */
    public void open(Player viewer) {
        InventoryView openedView;
        if (type != null) {
            openedView = viewer.openInventory(
                    ServiceContainer.getInstance().getService(PluginService.class)
                            .createInventory(type, title)
            );
        } else {
            openedView = viewer.openInventory(
                    ServiceContainer.getInstance().getService(PluginService.class)
                            .createInventory(size.getSize(), title)
            );
        }

        this.view = openedView;
        this.viewer = viewer;
        ServiceContainer.getInstance().getService(PluginService.class).registerEventListener(this);
        buttons.forEach((index, button) -> button.draw(this, index));
        placeButtons(viewer);
    }

    /**
     * Add a normal button to the menu.
     */
    public final void addButton(int index, ItemStack item) {
        addButton(index,item,null);
    }
    /**
     * Add a normal button to the menu.
     */
    public final void addButton(int index, ItemStack item, Consumer<InventoryClickEvent> function) {
        addButton(index, new Button(item, function));
    }
    /**
     * Add a normal button to the menu.
     */
    public final void addButton(int index, ItemStack item, Runnable function) {
        addButton(index, new Button(item, event->function.run()));
    }

    public final void addButton(int index,Plugin plugin, Supplier<ItemStack> async){
        addButton(index,plugin,async,null);
    }

    /**
     * Add an asynchronous button to the menu. The rendering of the item is deferred.
     */
    public final void addButton(int index, Plugin plugin, Supplier<ItemStack> async, Consumer<InventoryClickEvent> function) {
        addButton(index, new AsyncButton(async, plugin, function));
    }

    /**
     * Add a button to the menu.
     */
    public void addButton(int index, AButton button) {
        buttons.put(index, button);
        button.draw(this, index);
    }

    /**
     * Add a 'back' button to return to the previous menu.
     */
    public final void addBackButton(int index) {
        if (previous == null) return;
        addButton(
                index,
                new ItemBuilder(getBackButton())
                        .name("Retour")
                        .color(NamedTextColor.RED)
                        .build(),
                event -> {
                    if (viewer != null) previous.open(viewer);
                }
        );
    }

    /**
     * Add a 'back' button to return to the previous menu at default position.
     */
    public final void addBackButton() {
        addBackButton(size.getSize() - 9);
    }

    /**
     * Add a close button that will close the menu when clicked.
     */
    public final void addCloseButton(int index) {
        addButton(
                index,
                new ItemBuilder(getCloseButton())
                        .name("Fermer")
                        .color(NamedTextColor.DARK_RED)
                        .build(),
                event -> close()
        );
    }

    /**
     * Add a close button that will close the menu when clicked at default position.
     */
    public final void addCloseButton() {
        addCloseButton(size.getSize() - 1);
    }

    /**
     * Fill the entire inventory with the given item
     */
    public final void fill(ItemStack item) {
        for (int i = 0; i < size.getSize() - 1; i++) {
            addButton(i, item);
        }
    }

    /**
     * Fill the given indices with the given item
     */
    public final void fill(int[] indices, ItemStack item) {
        for (int index : indices) {
            addButton(index, item);
        }
    }

    /**
     * Fill the given indices with the given item without replacing existing buttons
     */
    public final void softFill(int[] indices, ItemStack item) {
        for (int index : indices) {
            if (!buttons.containsKey(index)) {
                addButton(index, item);
            }
        }
    }

    /**
     * Place buttons in the inventory. Override this hook in subclasses.
     */
    public void placeButtons(Player viewer) {
        // Default: do nothing (subclasses override)
    }

    /**
     * Close the inventory view.
     */
    public final void close() {
        if (view != null) {
            view.close();
        }
    }

    /**
     * Clear all buttons, without closing the view.
     */
    public void clear() {
        buttons.clear();
        if (view != null && view.getTopInventory() != null) {
            view.getTopInventory().clear();
        }
    }

    /**
     * Replace all buttons
     */
    public final void refresh(Player viewer) {
        clear();
        placeButtons(viewer);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Check that the click happened in this inventory
        if (event.getClickedInventory() == null || view == null) return;
        if (event.getClickedInventory() != view.getTopInventory()) return;

        // Cancel the event to freeze the items
        if (freezeBottom && event.getClickedInventory() == view.getBottomInventory()) {
            event.setCancelled(true);
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;

        AButton button = buttons.get(event.getSlot());
        if (button != null) {
            ServiceContainer.getInstance().getService(TaskScheduler.class).runLater(new Tick(1), () -> {
                try {
                    button.onClick(event);
                } catch (Exception e) {
                    logger.error("Error while clicking a menu button", e);
                    event.getWhoClicked().sendMessage(NamedTextColor.RED + "Une erreur interne est survenue.");
                    close();
                }
            });
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != null && view != null
                && event.getInventory().equals(view.getTopInventory())) {
            viewer = null;
            view = null;
            ServiceContainer.getInstance().getService(PluginService.class).unregisterEventListener(this);
        }
    }

    public boolean hasViewer() {
        return viewer != null;
    }

    public Map<Integer, AButton> getButtons() {
        return buttons;
    }

    public InventoryView getView() {
        return view;
    }
}
