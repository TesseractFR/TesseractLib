package onl.tesseract.tesseractlib.equipment;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
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
 * {@link onl.tesseract.tesseractlib.equipment.invocable.Invocable#Invocable(Equipment, EquipmentSlot, String)}
 * @see onl.tesseract.tesseractlib.equipment.invocable.Invocable
 */
public class Equipment implements Listener {
    static public final String pathToFolder = "plugins/Tesseract/joueurs/equipements/";
    final TPlayer player;

    public final HashSet<Invocable> invocables = new HashSet<>();
    public final List<Invocable> unblockedMainHand = new ArrayList<>();
    public final List<Invocable> unblockedOffHand = new ArrayList<>();
    public final List<Invocable> unblockedHelmet = new ArrayList<>();
    public final List<Invocable> unblockedChestplate = new ArrayList<>();
    public final List<Invocable> unblockedLeggings = new ArrayList<>();
    public final List<Invocable> unblockedBoots = new ArrayList<>();

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
        YamlConfiguration equipementData = this.serialize();

        try {
            equipementData.save(file);
        } catch (IOException e) {
            TesseractLib.logger().log(Level.SEVERE, "Failed to save equipment", e);
        }
    }

    public YamlConfiguration serialize()
    {
        final YamlConfiguration section = new YamlConfiguration();
        Collection<Map<?, ?>> yamlMap = new ArrayList<>();
        for (Invocable invocable : this.invocables) {
            yamlMap.add(invocable.save());
        }
        section.set("equipments", yamlMap);
        return section;
    }
}
