package onl.tesseract.tesseractlib.equipment.invocable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.lib.event.equipment.PlayerInvocableInvokeEvent;
import onl.tesseract.tesseractlib.menu.EquipmentMenu;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.tesseractlib.util.Util;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

/**
 * Represents an invocable item, like a jetpack or elytra.
 */
public abstract class Invocable implements Listener {
    private ItemStack item;
    protected final Equipment equipment;
    String name;
    protected final String localizedName;
    boolean invoked = false;
    public final EquipmentSlot slotType;
    /**
     * Slots in which the item currently is. From 0 to 8, or -1 if in off hand or armor slot.
     */
    public int slot = -1;
    protected final Player player;

    /**
     * Creates a new invocable that will be added to the given equipment.
     *
     * @param equipment Equipment of the player
     * @param slotType Slot type of this equipment.
     * @param localizedName Localized name of the itemStack
     */
    public Invocable(Equipment equipment, EquipmentSlot slotType, String localizedName)
    {
        // Register the item's event
        Bukkit.getServer().getPluginManager().registerEvents(this, TesseractLib.instance);
        this.equipment = equipment;
        this.slotType = slotType;
        this.localizedName = localizedName;

        this.equipment.invocables.add(this);
        this.player = this.equipment.getPlayer().getBukkitPlayer();
    }

    public Invocable(Equipment equipment, EquipmentSlot slotType, String localizedName, Map<String, Object> yamlMap)
    {
        // Call main constructor
        this(equipment, slotType, localizedName);
        // Load from yaml
        PlayerInventory inv = this.equipment.getPlayer().getBukkitPlayer().getInventory();
        this.invoked = (boolean) yamlMap.get("invoked");
        // Invoke
        if (this.invoked)
        {
            var that = this;
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (slotType == EquipmentSlot.HAND)
                    {
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

    /**
     * Loads an invocable from a yaml object.
     * @param yamlMap Yaml object
     * @param equipment Equipment to which the invocable belongs
     * @return The invocable instance
     */
    @Nullable
    public static Invocable newInvocable(Map<?, ?> yamlMap, Equipment equipment)
    {
        // Try to make a new instance of the Invocable via its class name
        try {
            String clazz = (String) yamlMap.get("className");
            // Get the parameterized constructor of the class corresponding to the name
            Class<?>[] args = new Class[] {Equipment.class, Map.class};
            Class<?> aClass = Class.forName(clazz);
            Constructor<?> constructor = aClass.getDeclaredConstructor(args);
            // Make a new instance with given parameters
            if (equipment.get((Class<? extends Invocable>) aClass) != null)
                return null;
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

    public Equipment getEquipment()
    {
        return equipment;
    }

    public Player getPlayer()
    {
        return player;
    }

    public int getInvocationPower()
    {
        return 0;
    }

    /**
     * Checks if a given item is an invocable
     *
     * @param item Item to check
     *
     * @return True if is an invocable
     */
    static public boolean isInvocable(ItemStack item)
    {
        return item != null && item.hasItemMeta() && (item.getItemMeta().hasLocalizedName() &&
                item.getItemMeta().getLocalizedName().contains("INVOCABLE"));
    }

    /**
     * Gets the Invocable instance of an item
     * @param equipment Equipment of the invocable
     * @param item Item of the invocable
     * @return Invocable instance
     */
    @Nullable
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
