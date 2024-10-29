package onl.tesseract.tesseractlib.util.menu;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.event.PlayerMenuOpenEvent;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class InventoryMenu implements Listener {
    static public final String NEW_LINE = " {nl} ";
    static final ItemStack buttonBack = getCustomHead("Retour", NamedTextColor.RED, "eyJ0aW1lc3RhbXAiOjE1MzQ0NTU2Njg3MTgsInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mN2FhY2FkMTkzZTIyMjY5NzFlZDk1MzAyZGJhNDMzNDM4YmU0NjQ0ZmJhYjVlYmY4MTgwNTQwNjE2NjdmYmUyIn19fQ==", "P1pFjz8nr9mcyMiBoisU0ON86W+7MG4K3ieuuLKrAvBwd11KFNrKqY7t0vp3kUVF0TNCaN/1oPEN27Ahl/L7l0yrM6c+tiPBQQkEGQiQpMqHPPM0bSVdT6m9Sv0zW7ZAytJXuRoK/JFr6InxMoAcd/lvhZvuNyL60nW7NRDtKYyac2/Z1X0Hk+aEI6XwuAE1g2SVkxyv7FWTrOWE+KO2Umv/w3GteV9fT6moHYOHhs0PmhqzrXHtqK+jfXB0b/eiVhQSBBiR4e9A8Svj+XJDzvH2csfZu9XeQ2kAUuJMQ09CpxvrBeQ1E8FFBFk8UAxQH/ANLMCcg+SsmJxnrR1SS45PP1BM+arm/VdmVsqzk60VBDyREhQmqtB+h6IDbYLOzIvggZhF3nQyolC/uklYy7SJ4WP5R3XuQtT/wPeS9s6BixtNhvbTVA7Yv02c8XTKMZpI4gN9sX2icbtOuYlIBf7w4aXNLBfi896RONuU4odS7X3mz7HwmNN2Zyu+XOPU8njTcbbIDxBWmTsfK/ROnFol19b4Vd8geyQSbFDZvsvqrLYS83mnBoQXODowHnSH8rRXAdQ0F8o/QkmUylz4tlSk5oi+y4Vv1EOKtut05HGyor38WFbO0niBYDv0EmHSO33m9vLYVJzoE65wXGT6bLhhrxdasBAr+WvkExtcgPg=");
    static final ItemStack buttonQuit = getCustomHead("Fermer", NamedTextColor.DARK_RED, "eyJ0aW1lc3RhbXAiOjE1ODcxNTc4ODI2NDMsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZkMDU2YzdlZGRhNTc4Y2MyYTEwYzU1NTBjNTY2YTVmNzQxNmEzMzZkZmQwNjIzMTZkOGMwYjI4NTFjZGU5M2MifX19", "bRUXqC6nIpHlgj3rWIW1STtWgK5BlDHcQq8PnZzXmmTjrLI5D9KzP9TKmq3v3PbtPIOvmoB+sIWIOTQp9gsPiqkWmtEh9cdyDe2ak7X5U+z79Got/p649Sd12W+Pg8iR0YVOJ7hOYRjFkYTi/f/2fi0V3YG+kIXDyNPkUXZHfEOdLnTRmUjbsT5EjM747+o3tX9bg7bX9i6Nsxo0/zIi4XPt3g8i0sBuo3ytgHb7WJQ/QxCtCoBg2M5SMr9pnk4bO9xtoLBmY/t2PcJqagMTY1QeWShtnKr4sHG7hXyXXf8hPFAnizv1zyMWsfuyPQCWqcVfHr3GDFj/Osxylov/faLOlWG2oCJCAODKVbZ6D+1nyPCgkO/ouK8WQ+U+Iyzzakx+zwaS7P22/9gw1PM8pfkF356bP6PWy1hHIb2EvYh1tVgkOCbUYIzdgkvGnts+qBAGA39Uqhz0lZ3IgW4uVSygbkcMqASGiwLfaWGsNsZF4Q24phOFIFiyO1oVw4eeV9GOPVYhKdT2RAePm4c4duoAing36vuwvV3oOUbJXUMHjMUPtXeLX5uJqJt2DetZCXiqIUyxqCXgCOLjJujjoyuyHpD/2m0IsPhg2a/dgfOcm/25K1lNaKOIgYhZ773eSyCZbkjN0h5bzZY8S87n3zTrHPqpGGUYzdRptZvvwAc=");
    static ItemStack checkmark = getCustomHead("Confirmer", NamedTextColor.GREEN, "ewogICJ0aW1lc3RhbXAiIDogMTU5MjY5NjU3ODgxMSwKICAicHJvZmlsZUlkIiA6ICI3MzgyZGRmYmU0ODU0NTVjODI1ZjkwMGY4OGZkMzJmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4cWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgyMzUwODA4NDg4YjQ0Y2EwNmVhNGE1NDUzY2QwZDM4N2I2MTg5YjVhZmRlY2EwYjhjNjJlNTVkNjg1MTBhMSIKICAgIH0KICB9Cn0=", "Z98pJmCgeb9xFh9V37708rOQz2j2zrOH4bkj+Yu7xcHPUnX3TObOAxQnp9iJoWoMDaQ2z9T6aJw3d/WXqQK8Ektqk0v59DrOHlv6Ei17uDHKoLrIlNfzT+YYY/x/cyeEd9EktFYp8GsvCHJi8YtJHV2msH7Z7j0d4IhB/MbE/ploYJfrIG3x8haziBWMdTLbZhr/ykGPh+d2uiCVHgjxj5w2HSjvhyaW5a0PDsJ9uiiPI8ZZaOLZHELZXHOPliy0cJrNH+KH4yV22vOR8XAzooSHv384xUMjkkrzYwGFK1HFaLelNXfve9odQC0Rjv0eEqFjnobiVq4wzC0Pl1nOUv0+Q4J/sOLs1GAy4P26usz1IZzfLgFw/Sx7h5xRuvdD6/6P50xr3jja8o3gH35DGID//ImivYniDz1eOTTfreosABjxVn3cTsyXOlDx+F9YHlYgwxmT4gcJXpTxFN52Ef1H9Qw8Moq6t7xbVMaOv4aHlSsMug94/5rHJV1745HQ/pDRcVNSE5qb8FTNtHJjhOsmm6ZLwnyf/5JAjkP2yp7qP+9Be6O/3f38v0Yr7UzWgLwvyJeoat/oKSif/ub6ujOxNTBcIVsPRNONaN64Rr44qm3zXU+F40E0wgLAx98SKLZw/JssVsTvHat8WHFMaZ2vMXmyZeNXS4c+Q+kPjdA=");
    final protected Inventory inventory;
    /**
     * Map of buttons. Each button is represented by an ItemStack, and maps to a function
     */
    final Map<Integer, AButton> buttons = new HashMap<>();
    /**
     * Reference to the parent menu, if there is one.
     */
    protected InventoryMenu previous;
    InventoryView view;
    @Getter
    Player viewer;
    boolean freezeBottom = true;

    /**
     * Creates a new menu.
     *
     * @param size  Size of the menu (multiple of 9)
     * @param title Name of the menu
     */
    @Deprecated
    public InventoryMenu(int size, String title) {
        this(size, Component.text(title));
    }


    /**
     * Creates a new menu.
     *
     * @param size  Size of the menu (multiple of 9)
     * @param title Name of the menu
     * @param color Color of the name menu
     */
    public InventoryMenu(int size, String title, NamedTextColor color) {
        this(size, Component.text(title, color));
    }


    /**
     * Creates a new menu.
     *
     * @param size  Size of the menu (multiple of 9)
     * @param title Name of the menu
     */
    public InventoryMenu(int size, Component title) {
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    /**
     * Creates a new menu.
     *
     * @param size     Size of the menu (multiple of 9)
     * @param title    Name of the menu
     * @param color    Color of the name menu
     * @param previous Parent menu. The "back" button will be added.
     */
    public InventoryMenu(int size, String title, NamedTextColor color, InventoryMenu previous) {
        this(size, title, color, previous, true);
    }

    /**
     * Creates a new menu.
     *
     * @param size     Size of the menu (multiple of 9)
     * @param title    Name of the menu
     * @param previous Parent menu. The "back" button will be added.
     */
    public InventoryMenu(int size, String title, InventoryMenu previous) {
        this(size, title, previous, true);
    }

    /**
     * Creates a new menu.
     *
     * @param size     Size of the menu (multiple of 9)
     * @param title    Name of the menu
     * @param previous Parent menu. The "back" button will be added.
     */
    public InventoryMenu(int size, Component title, InventoryMenu previous) {
        this(size, title, previous, true);

    }

    /**
     * Creates a new menu.
     *
     * @param size         Size of the menu (multiple of 9)
     * @param title        Name of the menu
     * @param color        Color of the name menu
     * @param previous     Parent menu. The "back" button will be added.
     * @param freezeBottom If the bottom inventory should be frozen.
     */
    public InventoryMenu(int size, String title, NamedTextColor color, InventoryMenu previous, boolean freezeBottom) {
        this(size, Component.text(title, color), previous, freezeBottom);
    }

    /**
     * Creates a new menu.
     *
     * @param size         Size of the menu (multiple of 9)
     * @param title        Name of the menu
     * @param previous     Parent menu. The "back" button will be added.
     * @param freezeBottom If the bottom inventory should be frozen.
     */
    public InventoryMenu(int size, String title, InventoryMenu previous, boolean freezeBottom) {
        this(size, Component.text(title), previous, freezeBottom);
    }

    /**
     * Creates a new menu.
     *
     * @param size         Size of the menu (multiple of 9)
     * @param title        Name of the menu
     * @param previous     Parent menu. The "back" button will be added.
     * @param freezeBottom If the bottom inventory should be frozen.
     */
    public InventoryMenu(int size, Component title, InventoryMenu previous, boolean freezeBottom) {
        this.inventory = Bukkit.createInventory(null, size, title);
        this.previous = previous;
        this.freezeBottom = freezeBottom;
    }

    public static ItemStack getCustomHead(String name, String data, String signature) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        if (name != null)
            meta.displayName(Component.text(name));

        PlayerProfile pp = Bukkit.createProfile(UUID.randomUUID());
        pp.setProperty(new ProfileProperty("textures", data, signature));
        meta.setPlayerProfile(pp);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack getCustomHead(String name, NamedTextColor color, String data, String signature) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        if (name != null)
            meta.displayName(Component.text(name, color));

        PlayerProfile pp = Bukkit.createProfile(UUID.randomUUID());
        pp.setProperty(new ProfileProperty("textures", data, signature));
        meta.setPlayerProfile(pp);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack getHead(UUID uuid) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(uuid));

        head.setItemMeta(meta);
        return head;
    }

    /**
     * Gets a player head asynchronously
     *
     * @param uuid     UUID of the owner
     * @param callback Callback called when the skin is applied, with the head as a parameter
     */
    public static void getHead(UUID uuid, Consumer<ItemStack> callback) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        new BukkitRunnable() {
            @Override
            public void run() {
                meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(uuid));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        head.setItemMeta(meta);

                        callback.accept(head);
                    }
                }.runTask(TesseractLib.instance);
            }
        }.runTaskAsynchronously(TesseractLib.instance);
    }

    /**
     * Opens a small menu asking the player to confirm or to cancel.
     *
     * @param player   Player to who the menu will be displayed
     * @param message  Confirmation message
     * @param backMenu The previous menu in case of cancellation
     * @param callback Callback to execute in case of confirmation
     */
    public static void openConfirmationMenu(Player player, Component message, InventoryMenu backMenu, Consumer<Void> callback) {
        InventoryMenu menu = new InventoryMenu(9, "Confirmer", backMenu);
        menu.fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        if (backMenu != null)
            menu.addBackButton();
        else
            menu.addQuitButton();
        menu.addButton(4, Material.LIME_CONCRETE, Component.text("Confirmer", NamedTextColor.GREEN), message, event -> {
            menu.close();
            callback.accept(null);
        });
        menu.open(player);
    }

    public static void openConfirmationMenu(Player player, String message, InventoryMenu backMenu, Consumer<Void> callback) {
        openConfirmationMenu(player, Component.text(message), backMenu, callback);
    }

    /**
     * Adds a passive item to the menu.
     *
     * @param index    Slot in which to put the item
     * @param material Material of the item
     * @param name     Custom display name of the item. Can be null
     * @param lore     Custom lore. Can be null
     */
    public void addInactiveButton(int index, Material material, String name, String lore) {
        addButton(index, new Button(new ItemBuilder(material)
                .name(name)
                .lore(new ItemLoreBuilder().append(lore).get())
                .build()));
    }

    public void addInactiveButton(int index, Material material, String name, String lore, boolean enchant) {
        addButton(index, new Button(new ItemBuilder(material)
                .name(name)
                .lore(new ItemLoreBuilder().append(lore).get())
                .enchanted(enchant)
                .build()));
    }

    public void addInactiveButtons(int[] indexes, Material material, String name, String lore) {
        for (int index : indexes) {
            addButton(index, new Button(new ItemBuilder(material)
                    .name(name)
                    .lore(new ItemLoreBuilder().append(lore).get())
                    .build()));
        }
    }

    public void addInactiveButton(int index, ItemStack item, String name, String lore) {
        addButton(index, new Button(new ItemBuilder(item)
                .name(name)
                .lore(new ItemLoreBuilder().append(lore).get())
                .build()));
    }

    public void addInactiveButton(int index, ItemStack item, String name, Component lore, boolean enchant) {
        addInactiveButton(index, new ItemBuilder(item)
                .name(name)
                .lore(new ItemLoreBuilder().append(lore).get())
                .enchanted(enchant)
                .build());
    }

    public void addInactiveButton(int index, ItemStack item) {
        addButton(index, new Button(item));
    }

    public void add(int index, Material material, Component name) {
        addButton(index, new Button(new ItemBuilder(material).name(name).build()));
    }

    public void add(int index, Material material, Component name, Component lore) {
        addButton(index, new Button(new ItemBuilder(material).name(name).lore(lore).build()));
    }

    public void add(int index, final ItemStack itemStack) {
        addButton(index, new Button(itemStack));
    }

    public void add(int[] indexes, Material material, Component name) {
        for (int index : indexes)
            addButton(index, new Button(new ItemBuilder(material).name(name).build()));
    }

    public void add(int[] indexes, Material material, Component name, Component lore) {
        for (int index : indexes)
            addButton(index, new Button(new ItemBuilder(material).name(name).lore(lore).build()));
    }

    public void add(int[] indexes, final ItemStack itemStack) {
        for (int index : indexes)
            addButton(index, new Button(itemStack));
    }

    /**
     * Adds an interactible button.
     *
     * @param item     Item to display
     * @param function Function to execute
     * @param <T>      The click event
     */
    public <T> void addButton(int index, ItemStack item, Consumer<InventoryClickEvent> function) {
        addButton(index, new Button(item, function));
    }

    protected void addButton(int index, Material material, Component nameComponent, Component loreComponent, Consumer<InventoryClickEvent> function) {
        ItemStack item = new ItemBuilder(material)
                .name(nameComponent)
                .lore(new ItemLoreBuilder().append(loreComponent).get())
                .build();
        this.addButton(index, item, function);
    }

    public void addButton(int index, Material material, String name, TextColor nameColor, String lore, TextColor loreColor, Consumer<InventoryClickEvent> function) {
        ItemStack item = new ItemBuilder(material)
                .name(name, nameColor)
                .lore(new ItemLoreBuilder().append(lore, loreColor).get())
                .build();
        this.addButton(index, item, function);
    }

    public void addButton(int index, Material material, String name, TextColor nameColor, String lore, Consumer<InventoryClickEvent> function) {
        ItemStack item = new ItemBuilder(material)
                .name(name, nameColor)
                .lore(new ItemLoreBuilder().append(lore).get())
                .build();
        this.addButton(index, item, function);
    }

    public <T> void addButton(int index, ItemStack item, boolean enchanted, Consumer<InventoryClickEvent> function) {
        ItemStack cloned = item.clone();
        if (enchanted)
            cloned.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        addButton(index, cloned, function);
    }

    public <T> void addButton(int index, ItemStack item, Component name, Component lore, Consumer<InventoryClickEvent> function) {
        if (item == null) return;
        item = new ItemBuilder(item)
                .name(name)
                .lore(lore)
                .build();
        addButton(index, item, function);
    }
    public <T> void addButton(int index, ItemStack item, Component name, List<Component> lore, Consumer<InventoryClickEvent> function) {
        if (item == null) return;
        item = new ItemBuilder(item)
                .name(name)
                .lore(lore)
                .build();
        addButton(index, item, function);
    }
    public <T> void addButton(int index, ItemStack item, String name, String lore, Consumer<InventoryClickEvent> function) {
        if (item == null) return;
        item = new ItemBuilder(item)
                .name(name)
                .lore(new ItemLoreBuilder().append(lore).get())
                .build();
        addButton(index, item, function);
    }

    public <T> void addButton(int index, Material material, String name, String lore, Consumer<InventoryClickEvent> function) {
        ItemStack item = new ItemBuilder(material)
                .name(name)
                .lore(new ItemLoreBuilder().append(lore).get())
                .build();
        this.addButton(index, item, function);
    }

    public <T> void addButton(int index, Material material, String name, String lore, boolean enchanted, Consumer<InventoryClickEvent> function) {
        ItemStack item = new ItemBuilder(material)
                .name(name)
                .lore(new ItemLoreBuilder().append(lore).get())
                .enchanted(enchanted)
                .build();
        this.addButton(index, item, function);
    }

    public void addButton(final int index, final AButton button) {
        buttons.put(index, button);
        button.draw(this, index);
    }

    void setItem(final int index, final ItemStack item) {
        inventory.setItem(index, item);
    }

    /**
     * Adds automatically a back button to return to the previous menu.
     */
    public void addBackButton() {
        addBackButton(this.inventory.getSize() - 9);
    }

    public void addBackButton(int index) {
        if (this.previous == null)
            return;
        this.addButton(index, new ItemBuilder(buttonBack).name("Retour", NamedTextColor.RED).build()
                , event -> this.previous.open(this.viewer));
    }

    /**
     * Adds a custom back button that executes a function
     *
     * @param function Function to execute
     * @param <T>      void
     */
    @Deprecated
    public <T> void addBackButton(Consumer<T> function) {
        addButton(inventory.getSize() - 9, new ItemBuilder(buttonBack).name("Retour", NamedTextColor.RED).build(), event -> {
            this.close();
            function.accept(null);
        });
    }

    public <T> void addBackButton(Runnable function) {
        addButton(inventory.getSize() - 9, new ItemBuilder(buttonBack).name("Retour", NamedTextColor.RED).build(), event -> {
            this.close();
            function.run();
        });
    }

    public void addQuitButton() {
        addQuitButton(this.inventory.getSize() - 1);
    }

    public void addQuitButton(int index) {
        this.addButton(index, buttonQuit, event -> this.close());
    }

    public void fillNoReplace(Material material, String name) {
        ItemStack item = new ItemBuilder(material, Component.text(name)).build();
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, item);
            }
        }
    }

    public void fill(Material material, String name, String lore) {
        ItemStack item = new ItemBuilder(material)
                .name(name)
                .lore(new ItemLoreBuilder().append(lore).get())
                .build();

        for (int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, item);
        }
    }

    public void fill(Material material, String name) {
        ItemStack item = new ItemBuilder(material, Component.text(name)).build();

        for (int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, item);
        }
    }

    public void fill(Material material, String name, Component lore) {
        ItemStack item = new ItemBuilder(material).name(name).lore(lore).build();

        for (int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, item);
        }
    }

    public void fill(final AButton button) {
        for (int i = 0; i < this.inventory.getSize(); i++) {
            addButton(i, button);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Chek that the click happened in this inventory
        if (!event.getInventory().equals(this.inventory))
            return;
        // Cancel the event to freeze the items.
        if (!(event.getClickedInventory() != null && !event.getClickedInventory().equals(inventory) && !freezeBottom))
            event.setCancelled(true);

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(this.inventory))
            return;

        if (event.getCurrentItem() == null)
            return;
        // If the clicked item is a button
        if (event.getCurrentItem() != null && buttons.containsKey(event.getSlot())) {
            // Accept the consumer, with a delay of one tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    var button = buttons.get(event.getSlot());
                    button.onClick(event);
                }
            }.runTaskLater(TesseractLib.instance, 1);
        } else if (event.getCurrentItem().equals(buttonBack)) {
            this.close();
            if (this.previous != null)
                this.previous.open(this.viewer);
        }
    }

    /**
     * Opens the menu
     */
    public void open(Player viewer) {
        PlayerMenuOpenEvent event = new PlayerMenuOpenEvent(this, viewer);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            event.getMenu().view = event.getPlayer().openInventory(event.getMenu().inventory);
            event.getMenu().viewer = event.getPlayer();
        }
        Bukkit.getPluginManager().registerEvents(this, TesseractLib.instance);
    }

    public void close() {
        if (this.view != null)
            this.view.close();
        this.buttons.clear();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(this.inventory))
            HandlerList.unregisterAll(this);
    }

    /**
     * Clears the menu
     */
    public void clear() {
        this.buttons.clear();
        this.inventory.clear();
    }

    public boolean hasViewers() {
        return !inventory.getViewers().isEmpty();
    }


}
