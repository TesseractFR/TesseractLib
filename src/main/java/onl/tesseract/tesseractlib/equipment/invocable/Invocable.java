package onl.tesseract.tesseractlib.equipment.invocable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.event.PlayerInvocableInvokeEvent;
import onl.tesseract.tesseractlib.menu.EquipmentMenu;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Represents an invocable item, like a jetpack or elytra.
 */
public abstract class Invocable implements Listener {
    ItemStack item;
    protected Equipment equipment;
    String name;
    protected String localizedName;
    boolean invoked = false;
    public EquipmentSlot slotType;
    /**
     * Slots in which the item currently is. From 0 to 8, or -1 if in off hand or armor slot.
     */
    public int slot = -1;
    protected Player player;

    /**
     * Creates a new invocable that will be added to the given equipment.
     * @param equipment Equipment of the player
     * @param slotType Slot type of this equipment.
     * @param localizedName Localized name of the itemStack
     * @param item itemStack model.
     */
    public Invocable(Equipment equipment, EquipmentSlot slotType, String localizedName, ItemStack item) {
        // Register the item's event
        Bukkit.getServer().getPluginManager().registerEvents(this, TesseractLib.instance);
        this.equipment = equipment;
        this.slotType = slotType;
        this.localizedName = localizedName;
        // Create the itemStack
        ItemMeta meta = item.getItemMeta();
        meta.setLocalizedName(localizedName);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        this.item = item;

        this.equipment.invocables.add(this);
        this.player = this.equipment.getPlayer().getBukkitPlayer();
    }

    public Invocable(Equipment equipment, EquipmentSlot slotType, String localizedName, ItemStack item, Map<String, Object> yamlMap) {
        // Call main constructor
        this(equipment, slotType, localizedName, item);
        // Load from yaml
        PlayerInventory inv = this.equipment.getPlayer().getBukkitPlayer().getInventory();
        this.invoked = (boolean) yamlMap.get("invoked");
        // Invoke
        if (this.invoked) {
            var that = this;
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (slotType == EquipmentSlot.HAND) {
                        slot = (int) yamlMap.get("slot");
                        if (slot > -1)
                            inv.setItem(slot, getItem());
                        else if (slot == -1)
                            inv.setItem(EquipmentSlot.OFF_HAND, getItem());
                        else
                            return;
                    }else {
                        inv.setItem(slotType, getItem());
                    }
                    if (excludesOther())
                        equipment.set(slotType, that);
                    onInvoke(false);
                }
            }.runTask(TesseractLib.instance);
        }
    }

    /**
     * Save the invocable into a map
     * @return Map that can be added to a yaml list
     */
    public Map<String, Object> save() {
        // If it is a secondary invocable, that is not in its original slot, uninvoke it to avoid problems
        Map<String, Object> map = new HashMap<>();
        map.put("className", this.getClass().getName());
        map.put("invoked", this.isInvoked());
        map.put("slot", this.slot);
        return map;
    }

    /**
     * Returns the itemStack this invocable represents
     * @return Itemstack of this invokable
     */
    public ItemStack getItem()
    {
        return item;
    }

    public void setItem(ItemStack item)
    {
        this.item = item;
    }

    /**
     * Called when this invocable is uninvoked.
     * @param manualUninvocation True if uninvoked by the player himself.
     */
    protected abstract void onUninvoke(boolean manualUninvocation);
    /**
     * Called when this invocable is invoked.
     * @param manualInvocation True if invoked by the player himself.
     */
    protected abstract void onInvoke(boolean manualInvocation);

    /**
     * Invoke the invokable at its default slot. For mainHand, it will take the slot selected by the player
     */
    public boolean invoke() {
        return invoke(true);
    }
    public boolean invoke(boolean manualInvocation) {
        PlayerInvocableInvokeEvent event = new PlayerInvocableInvokeEvent(equipment.getPlayer(), this,
                                                                          manualInvocation);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;
        PlayerInventory inv = this.equipment.getPlayer().getBukkitPlayer().getInventory();
        // Remove any present invokable
        this.equipment.uninvoke(this.slotType);
        // If there is already an item at that spot
        if (inv.getItem(this.slotType) != null) {
            // If there is no slot to move this item, cancel
            if (inv.firstEmpty() == -1)
                return false;
            // Move the item, and put the invokable
            ItemStack other = inv.getItem(this.slotType);
            inv.setItem(this.slotType, this.item);
            inv.addItem(other);
        }else {
            inv.setItem(this.slotType, this.item);
        }
        this.invoked = true;
        if (this.slotType == EquipmentSlot.HAND)
            this.slot = inv.getHeldItemSlot();
        this.equipment.set(this.slotType, this);
        // this.equipment.getPlayer().getBukkitPlayer().sendMessage(ChatFormat.Equipment + "L'équipement a été invoqué.");
        this.onInvoke(manualInvocation);
        return true;
    }

    /**
     * Invoke the mainHand invokable at the given index
     * @param index index of the slot in the action bar. [0,8]
     */
    public void invoke(int index) {
        PlayerInvocableInvokeEvent event = new PlayerInvocableInvokeEvent(equipment.getPlayer(), this, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        if (index < 0 || index > 8) return;
        if (this.slotType != EquipmentSlot.HAND) return;
        PlayerInventory inv = this.equipment.getPlayer().getBukkitPlayer().getInventory();
        if (invoked)
            this.uninvoke();
        // Remove any present invokable
        if (this.excludesOther())
            this.equipment.uninvoke(this.slotType);
        if (inv.getItem(index) != null) {
            // If the present item is an invocable, uninvoke it
            if (isInvocable(inv.getItem(index)))
                asInvocable(equipment, inv.getItem(index)).uninvoke();
            // If there is no slot to move this item, cancel
            if (inv.firstEmpty() == -1)
                return;
            // Move the item, and put the invokable
            ItemStack other = inv.getItem(index);
            inv.setItem(index, this.item);
            if (other != null)
                inv.addItem(other);
        }else
            inv.setItem(index, this.item);

        this.invoked = true;
        if (this.excludesOther())
            this.equipment.set(EquipmentSlot.HAND, this);
        // this.equipment.getPlayer().getBukkitPlayer().sendMessage(ChatFormat.Equipment + "L'équipement a été invoqué.");
        this.slot = index;
        this.onInvoke(true);
    }

    /**
     * Uninvoke the item.
     */
    public void uninvoke() {
        PlayerInventory inv = this.equipment.getPlayer().getBukkitPlayer().getInventory();

        // Off hand
        if (this.slotType == EquipmentSlot.HAND && slot == -1)
            inv.setItem(EquipmentSlot.OFF_HAND, null);
            // Armor
        else if (inv.getItem(this.slotType) != null && Invocable.isInvocable(inv.getItem(this.slotType)))
            inv.setItem(this.slotType, null);

        // Remove in any content slot
        for (int i = 0; i < inv.getContents().length; i++)
        {
            ItemStack item = inv.getContents()[i];
            if (Invocable.isInvocable(item))
            {
                if (item.getItemMeta().getLocalizedName().equals(this.localizedName))
                {
                    inv.setItem(i, null);
                }
            }
        }

        this.invoked = false;
        if (this.excludesOther())
            this.equipment.set(this.slotType, null);
        this.slot = -1;

        this.onUninvoke(true);
    }

    int findItemIndex()
    {
        PlayerInventory inv = equipment.getPlayer().getBukkitPlayer().getInventory();
        for (int i = 0; i < 9; i++)
        {
            if (inv.getItem(i) != null && isInvocable(inv.getItem(i)))
            {
                Invocable invoc = asInvocable(equipment, inv.getItem(i));
                if (invoc != null && invoc.localizedName.equals(localizedName))
                    return i;
            }
        }
        return -1;
    }

    /**
     * Called when an offHand or mainHand invocable is used.
     * @param event Event of the interaction.
     */
    protected abstract void use(PlayerInteractEvent event);

    /**
     * Called when the item is clicked in the inventory.
     * @param event Event of the interaction
     */
    protected abstract void useInInventory(InventoryClickEvent event);

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (! equipment.getPlayer().getOfflinePlayer().getUniqueId().equals(event.getWhoClicked().getUniqueId())) return;

        // Cancel the event if the player move the item with hotbar buttons
        if (event.getClick() == ClickType.NUMBER_KEY)
        {
            if (isInvocable(event.getCursor()) ||
                    isInvocable(equipment.getPlayer().getBukkitPlayer().getInventory().getItem(event.getHotbarButton()))) {
                event.setCancelled(true);
                return;
            }
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        // Check that it is a invokable item
        if (item.hasItemMeta() && item.getItemMeta().hasLocalizedName() &&
                item.getItemMeta().getLocalizedName().equals(this.localizedName)) {
            event.setCancelled(true);

            if (! Objects.equals(event.getInventory().getHolder(), this.equipment.getPlayer().getBukkitPlayer()))
                return;

            final Invocable finalThis = this;
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    // If shift click, uninvoke it
                    if (event.isShiftClick()) {
                        uninvoke();
                        equipment.getPlayer().sendMessage(ChatFormats.EQUIPMENT.append(Component.text("Équipement désinvoqué. Vous pouvez ré-invoquer un équipement via "))
                                                          .append(Component.text("/equipement", NamedTextColor.GOLD)));
                    }else if (slotType == EquipmentSlot.HAND) {
                        EquipmentMenu menu = new EquipmentMenu(finalThis.equipment.getPlayer());
                        menu.mainHandInvocationMenu(finalThis, finalThis.equipment.getPlayer().getBukkitPlayer());
                    }else {
                        useInInventory(event);
                    }
                }
            }.runTaskLater(TesseractLib.instance, 1);
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (! event.getPlayer().equals(equipment.getPlayer().getBukkitPlayer())) return;
        if (! event.hasItem()) return;
        ItemStack item = event.getItem();
        // Check that it is a invokable item
        assert item != null;
        if (item.hasItemMeta() && item.getItemMeta().hasLocalizedName() &&
                item.getItemMeta().getLocalizedName().equals(this.localizedName)) {
            this.use(event);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void hanging(PlayerInteractEntityEvent event)
    {
        if (event.getRightClicked() instanceof Hanging)
        {
            if (isInvocable(event.getPlayer().getInventory().getItem(event.getHand())))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (! event.getPlayer().equals(equipment.getPlayer().getBukkitPlayer())) return;
        ItemStack item = event.getItemDrop().getItemStack();
        // Check that it is a invokable item
        if (item.hasItemMeta() && item.getItemMeta().hasLocalizedName() &&
                item.getItemMeta().getLocalizedName().equals(this.localizedName)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (! event.getPlayer().equals(equipment.getPlayer().getBukkitPlayer())) return;
        ItemStack main = event.getMainHandItem();
        ItemStack off = event.getOffHandItem();

        if (main != null && isInvocable(main)) {
            Invocable invocable = asInvocable(equipment, main);
            if (invocable != null && invocable.excludesOther()) {
                event.setCancelled(true);
                return;
            } else if (invocable != null) {
                // Wait for the event to finish, and get the new slot of the invocable
                new BukkitRunnable() {
                    @Override
                    public void run()
                    {
                        invocable.slot = Util.getSlot(equipment.getPlayer().getBukkitPlayer().getInventory(), invocable.getItem());
                    }
                }.runTaskLater(TesseractLib.instance, 1);
            }
        }

        if (off != null && isInvocable(off)) {
            Invocable invocable = asInvocable(equipment, off);
            if (invocable != null && invocable.excludesOther())
                event.setCancelled(true);
            else if (invocable != null) {
                // Wait for the event to finish, and get the new slot of the invocable
                new BukkitRunnable() {
                    @Override
                    public void run()
                    {
                        invocable.slot = Util.getSlot(equipment.getPlayer().getBukkitPlayer().getInventory(), invocable.getItem());
                    }
                }.runTaskLater(TesseractLib.instance, 1);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (! event.getPlayer().equals(equipment.getPlayer().getBukkitPlayer())) return;
        // Remove the invocable when the player leaves
        if (this.isInvoked()) {
            PlayerInventory inv = this.equipment.getPlayer().getBukkitPlayer().getInventory();
            if (this.slotType == EquipmentSlot.HAND && slot >= 0)
                inv.clear(this.slot);
            else if (this.slotType == EquipmentSlot.HAND && slot == -1)
                inv.setItem(EquipmentSlot.OFF_HAND, null);
            else
                inv.setItem(this.slotType, null);
            this.onUninvoke(false);
        }

        HandlerList.unregisterAll(this);
    }

    /**
     * Loads an invocable from a yaml object.
     * @param yamlMap Yaml object
     * @param equipment Equipment to which the invocable belongs
     * @return The invocable instance
     */
    public static Invocable newInvocable(Map<?, ?> yamlMap, Equipment equipment)
    {
        // Try to make a new instance of the Invocable via its class name
        try {
            String clazz = (String) yamlMap.get("className");
            // Get the parameterized constructor of the class corresponding to the name
            Class<?>[] args = new Class[] {Equipment.class, Map.class};
            Constructor<?> constructor = Class.forName(clazz).getDeclaredConstructor(args);
            // Make a new instance with given parameters
            Object obj = constructor.newInstance(equipment, yamlMap);
            return (Invocable) obj;
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            TesseractLib.logger().log(Level.SEVERE, "Error while loading equipment of player " + equipment.getPlayer().getBukkitPlayer().getUniqueId(), e);
            return null;
        }
    }

    public boolean isInvoked()
    {
        return invoked;
    }

    /**
     * Does this invocable excludes other invocables when invoked
     */
    public boolean excludesOther() { return true; }

    /**
     * Updates the item instance of this invocable in the inventory, to match the new item model.
     */
    public void updateItemInInventory() {
        if (!invoked) return;
        PlayerInventory inv = equipment.getPlayer().getBukkitPlayer().getInventory();
        if (this.slotType != EquipmentSlot.HAND)
            inv.setItem(slotType, item);
        else if (slot != -1)
            inv.setItem(slot, item);
        else
            inv.setItem(EquipmentSlot.OFF_HAND, item);
    }

    public String getLocalizedName()
    {
        return localizedName;
    }

    public int getSlot()
    {
        return slot;
    }

    /**
     * Checks if a given item is an invocable
     * @param item Item to check
     * @return True if is an invocable
     */
    static public boolean isInvocable(ItemStack item) {
        return item != null && item.hasItemMeta() && (item.getItemMeta().hasLocalizedName() &&
                item.getItemMeta().getLocalizedName().contains("INVOCABLE"));
    }

    /**
     * Gets the Invocable instance of an item
     * @param equipment Equipment of the invocable
     * @param item Item of the invocable
     * @return Invocable instance
     */
    static public Invocable asInvocable(Equipment equipment, ItemStack item) {
        if (isInvocable(item)) {
            String name = item.getItemMeta().getLocalizedName();
            for (Invocable i : equipment.invocables) {
                if (i.localizedName.equals(name))
                    return i;
            }
        }
        return null;
    }
}
