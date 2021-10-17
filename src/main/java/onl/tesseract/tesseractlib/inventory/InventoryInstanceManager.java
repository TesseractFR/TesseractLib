package onl.tesseract.tesseractlib.inventory;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class InventoryInstanceManager {
    private static final String FOLDER_PATH = "plugins/Tesseract/inventories/";
    private static final Map<String, InventoryInstanceConfiguration> configurations = new HashMap<>();
    private static final Map<UUID, String> playerToConfig = new HashMap<>();

    public static void loadConfigurations()
    {
        File configFile = new File(FOLDER_PATH + "config.json");
        File folder = new File(FOLDER_PATH);
        if (!configFile.exists())
        {
            if (!folder.exists())
                new File(FOLDER_PATH).mkdirs();
            return;
        }

        try
        {
            InventoryInstanceConfiguration.load(configFile)
                                          .forEach(config -> configurations.put(config.getName(), config));
        }
        catch (IOException e)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to load inventories configurations", e);
        }
        File playersFile = new File(FOLDER_PATH + "players.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playersFile);
        for (String key : yaml.getKeys(false))
        {
            try
            {
                UUID uuid = UUID.fromString(key);
                String configName = yaml.getString(key);
                playerToConfig.put(uuid, configName);
            }catch (IllegalArgumentException e)
            {
                TesseractLib.logger().log(Level.SEVERE, "Failed to load player's inventory configuration for uuid " + key, e);
            }
        }
    }

    /**
     * Saves the current player's inventory in the configured inventory instance
     */
    public static void save(final Player player)
    {
        InventoryInstanceConfiguration config = getInstance(player);
        File file = new File(FOLDER_PATH + config.getName() + "/" + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("items", player.getInventory().getContents());

        ConfigurationSection invocableSection = yaml.createSection("invocables");
        TPlayer tPlayer = TPlayer.get(player);
        for (Invocable invocable : tPlayer.getEquipment().getInvocables())
        {
            if (!invocable.isInvoked())
                continue;
            invocableSection.set(invocable.getLocalizedName(), invocable.getSlot());
        }
        try
        {
            yaml.save(file);
        }
        catch (IOException e)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to save player's inventory", e);
        }
    }

    public static InventoryInstanceConfiguration getInstance(final Player player)
    {
        if (playerToConfig.containsKey(player.getUniqueId()))
            return configurations.get(playerToConfig.get(player.getUniqueId()));
        return new InventoryInstanceConfiguration("default", false, Collections.emptyList(), Collections.emptyMap());
    }
}

