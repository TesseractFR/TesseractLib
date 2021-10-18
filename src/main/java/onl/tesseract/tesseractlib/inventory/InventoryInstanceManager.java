package onl.tesseract.tesseractlib.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class InventoryInstanceManager {
    private static final String FOLDER_PATH = "plugins/Tesseract/inventories/";
    private static final Map<String, InventoryInstanceConfiguration> configurations = new HashMap<>();
    private static final Map<UUID, String> playerToConfig = new HashMap<>();

    static {
        configurations.put("default", new InventoryInstanceConfiguration("default", false, Collections.emptyList(), Collections.emptyMap()));
    }

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
            }
            catch (IllegalArgumentException e)
            {
                TesseractLib.logger().log(Level.SEVERE, "Failed to load player's inventory configuration for uuid " + key, e);
            }
        }
    }

    public static void save() throws IOException
    {
        File playersFile = new File(FOLDER_PATH + "players.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        playerToConfig.forEach(((uuid, config) -> {
            yaml.set(uuid.toString(), config);
        }));
        yaml.save(playersFile);

        File configFile = new File(FOLDER_PATH + "config.json");
        InventoryInstanceConfigurations configs = new InventoryInstanceConfigurations(configurations.values()
                .stream().toList());
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, configs);
    }

    /**
     * Saves the current player's inventory in the configured inventory instance
     */
    public static void save(final Player player)
    {
        InventoryInstanceConfiguration config = getSelectedConfig(player);
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

    /**
     * Load and apply a configuration to a player. The current player's inventory will be saved and replaced
     */
    public static void selectConfig(final Player player, final String configName)
    {
        if (!configurations.containsKey(configName))
            throw new IllegalArgumentException("This configuration does not exist");
        if (configName.equals(getSelectedConfig(player).getName()))
            return;
        InventoryInstanceConfiguration config = configurations.get(configName);

        save(player);
        if (configName.equals("default"))
            playerToConfig.remove(player.getUniqueId());
        else
            playerToConfig.put(player.getUniqueId(), configName);
        player.getInventory().clear();
        TPlayer tPlayer = TPlayer.get(player);
        tPlayer.getEquipment().uninvokeAll();

        File file = new File(FOLDER_PATH + configName + "/" + player.getUniqueId() + ".yml");
        if (file.exists())
        {
            applyExistingConfig(tPlayer, file, config);
        }
        else
        {
            applyNewConfig(tPlayer, config);
        }
    }

    private static void applyNewConfig(final TPlayer player, final InventoryInstanceConfiguration config)
    {
        config.getItems().forEach(((material, integer) -> {
            ItemStack item = new ItemStack(material, integer);
            player.getBukkitPlayer().getInventory().addItem(item);
        }));
    }

    private static void applyExistingConfig(final TPlayer player, final File file, final InventoryInstanceConfiguration config)
    {
        ConfigurationSection yaml = YamlConfiguration.loadConfiguration(file);

        // Load items
        var content = TPlayer.loadInventory(yaml, "items");
        player.getBukkitPlayer().getInventory().setContents(content);

        // Load invocables
        if (config.isRestrictInvocables())
            return;
        ConfigurationSection invocableSection = yaml.getConfigurationSection("invocables");
        if (invocableSection == null)
            return;
        for (String invocableName : invocableSection.getKeys(false))
        {
            if (!config.getInvocables().contains(invocableName))
                continue;
            int slot = invocableSection.getInt(invocableName);
            player.getEquipment().get(invocableName)
                  .ifPresent(invocable -> {
                      if (slot == -1)
                          invocable.invoke(false);
                      else
                          invocable.invoke(slot);
                  });
        }
    }

    public static InventoryInstanceConfiguration getSelectedConfig(final Player player)
    {
        if (playerToConfig.containsKey(player.getUniqueId()))
            return configurations.get(playerToConfig.get(player.getUniqueId()));
        return new InventoryInstanceConfiguration("default", false, Collections.emptyList(), Collections.emptyMap());
    }

    public static Collection<InventoryInstanceConfiguration> getAllConfigs()
    {
        return configurations.values();
    }
}

