package onl.tesseract.tesseractlib.equipment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.invocable.Boussole;
import onl.tesseract.tesseractlib.equipment.invocable.Elytra;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Represents the equipment container of the player. It is created only when a player joins, and is unavailable when
 * the player is offline.
 * Addind a new invokable is done directly by creating a new Invocable, in Invocable subclasses :
 * {@link onl.tesseract.tesseractlib.equipment.invocable.Invocable#Invocable(Equipment, EquipmentSlot, String, ItemStack)}
 * @see onl.tesseract.tesseractlib.equipment.invocable.Invocable
 */
public class Equipment implements Listener {
    static public final String pathToFolder = "plugins/Tesseract/joueurs/equipements/";
    TPlayer player;
    int invocationPower = 100;

    public HashSet<Invocable> invocables = new HashSet<>();
    public List<Invocable> unblockedMainHand = new ArrayList<>();
    public List<Invocable> unblockedOffHand = new ArrayList<>();
    public List<Invocable> unblockedHelmet = new ArrayList<>();
    public List<Invocable> unblockedChestplate = new ArrayList<>();
    public List<Invocable> unblockedLeggings = new ArrayList<>();
    public List<Invocable> unblockedBoots = new ArrayList<>();

    public Invocable mainHand;
    public Invocable offHand;
    public Invocable helmet;
    public Invocable chestplate;
    public Invocable leggings;
    public Invocable boots;

    public Equipment(TPlayer player) {
        this.player = player;
        Bukkit.getServer().getPluginManager().registerEvents(this, TesseractLib.instance);
    }

    /**
     * Returns a set of all invocables owned by this equipment.
     * @return Set of invocables.
     */
    public Iterable<Invocable> getInvocables()
    {
        return invocables;
    }

    /**
     * Gets the TPlayer owning this equipment set.
     * @return Owner
     */
    public TPlayer getPlayer()
    {
        return player;
    }

    /**
     * Gets the amount of invocation power.
     * @return 0 <= power <= 100
     */
    public int getInvocationPower()
    {
        return invocationPower;
    }

    /**
     * Adds or remove some invocation power. If it reaches 0, all invocables are removed.
     * @param count power to add. Can be negative.
     */
    public void addInvocationPower(int count) {
        invocationPower += count;
        if (invocationPower > 100)
            invocationPower = 100;
        if (invocationPower < 0)
            invocationPower = 0;

        if (invocationPower == 0) {
            this.uninvokeAll();
            this.getPlayer().getBukkitPlayer().sendMessage(
                    ChatFormats.EQUIPMENT.append(Component.text("Votre pouvoir d'invocation est épuisé, vos équipements ont été désinvoqués."
                                                                        + " Rechargez votre pouvoir dans le menu d'équipement.")));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (! event.getEntity().getUniqueId().equals(player.getOfflinePlayer().getUniqueId())) return;
        boolean hasEquipment = false;
        Equipment eq = TPlayer.get(event.getEntity()).getEquipment();
        // When the player dies, keep all invokable objects
        for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext(); ) {
            ItemStack drop = iterator.next();
            if (Invocable.isInvocable(drop)) {
                // Remove from dropped items
                iterator.remove();
                Invocable invoc = Invocable.asInvocable(eq, drop);
                if (invoc instanceof Boussole || invoc instanceof Elytra ){
                    event.getItemsToKeep().add(drop);
                    continue;
                }
                if (! hasEquipment) {
                    this.addInvocationPower(-5);
                    if (invocationPower > 0)
                    {
                        var comp = ChatFormats.EQUIPMENT
                                .append(Component.text("Vous êtes mort avec au moins un objet invocable sur vous. Vous avez perdu 5% de"
                                                               + " pouvoir d'invocation. Pouvoir restant: "))
                                .append(Component.text(this.invocationPower + "%").color(NamedTextColor.GOLD));
                        this.getPlayer().getBukkitPlayer().sendMessage(comp);
                    }
                }
                // Add to kept items
                if (invocationPower > 0)
                    event.getItemsToKeep().add(drop);
                hasEquipment = true;
            }
        }
    }

    public void uninvokeAll() {
        for (Invocable i : invocables) {
            if (i.isInvoked())
                i.uninvoke();
        }
    }

    /**
     * Loads the equipment of a TPlayer. Can provoke equipment invocation
     * @param player Player whose equipment will be loaded
     * @return The Equipment instance that holds the invocables
     */
    static public Equipment load(TPlayer player)
    {
        // Load the file
        Equipment eq = new Equipment(player);
        File file = new File(pathToFolder + player.getBukkitPlayer().getUniqueId()
                + "_equipement.yml");
        FileConfiguration equipementData = YamlConfiguration.loadConfiguration(file);
        eq.invocationPower = equipementData.getInt("invocationPower");
        // Load equipments
        List<Map<?,?>> equipments = equipementData.getMapList("equipments");
        for (Map<?,?> equipment : equipments) {
            // Create the equipment from the yaml object
            Invocable.newInvocable(equipment, eq);
        }
        return eq;
    }

    /**
     * Saves the player's equipment into the yaml file
     */
    public void save() {
        File file = new File(pathToFolder + player.getOfflinePlayer().getUniqueId()
                + "_equipement.yml");
        FileConfiguration equipementData = YamlConfiguration.loadConfiguration(file);
        Collection<Map<?, ?>> yamlMap = new ArrayList<>();
        for (Invocable invocable : this.invocables) {
            yamlMap.add(invocable.save());
        }
        equipementData.set("equipments", yamlMap);
        equipementData.set("invocationPower", this.invocationPower);
        try {
            equipementData.save(file);
        } catch (IOException e) {
            TesseractLib.logger().log(Level.SEVERE, "Failed to save equipment", e);
        }
    }

    /**
     * Sets an invocable in a slot.
     * @param slot Slot that holds the invocable
     * @param invokable Invocable to invoke.
     */
    public void set(EquipmentSlot slot, Invocable invokable) {
        switch (slot)
        {
            case HEAD -> this.helmet = invokable;
            case CHEST -> this.chestplate = invokable;
            case LEGS -> this.leggings = invokable;
            case FEET -> this.boots = invokable;
            case HAND -> this.mainHand = invokable;
            case OFF_HAND -> this.offHand = invokable;
            default -> {
            }
        }
    }

    public void uninvoke(EquipmentSlot slot) {
        if (this.get(slot) != null)
            this.get(slot).uninvoke();
    }

    /**
     * Recovers the invocable at that slot
     * @param slot Slot of the invocable
     * @return Invocable, or null
     */
    public Invocable get(EquipmentSlot slot) {
        return switch (slot)
                {
                    case HEAD -> this.helmet;
                    case CHEST -> this.chestplate;
                    case LEGS -> this.leggings;
                    case FEET -> this.boots;
                    case HAND -> this.mainHand;
                    case OFF_HAND -> this.offHand;
                    default -> null;
                };
    }

    /**
     * Gets an invocable by class
     * @param clazz Class of the invocable
     * @return The invocable, or null
     */
    @Nullable
    public Invocable get(Class<?> clazz) {
        for (Invocable i : invocables) {
            if (i.getClass().equals(clazz))
                return i;
        }
        return null;
    }

    public Optional<Invocable> get(final String localizedName) {
        for (Invocable i : invocables) {
            if (i.getLocalizedName().equals(localizedName))
                return Optional.of(i);
        }
        return Optional.empty();
    }

    /**
     * Gets an invocable by class or super-class
     * @param clazz Class of the invocable
     * @return The invocable that is an instance of clazz, or null
     */
    @Nullable
    public Invocable getLike(Class<?> clazz) {
        for (Invocable i : invocables) {
            if (clazz.isInstance(i))
                return i;
        }
        return null;
    }

    /**
     * Removes an invocable from this equipment
     * @param invocable The object to remove
     */
    public void remove(Invocable invocable)
    {
        if (invocable.isInvoked())
            invocable.uninvoke();
        // Remove from lists
        unblockedHelmet.remove(invocable);
        unblockedMainHand.remove(invocable);
        unblockedBoots.remove(invocable);
        unblockedChestplate.remove(invocable);
        unblockedLeggings.remove(invocable);
        unblockedOffHand.remove(invocable);

        invocables.remove(invocable);
        this.save();
    }
}
