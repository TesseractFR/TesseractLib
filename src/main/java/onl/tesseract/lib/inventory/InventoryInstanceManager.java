package onl.tesseract.lib.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import onl.tesseract.lib.equipment.EquipmentService;
import onl.tesseract.lib.equipment.Invocable;
import onl.tesseract.lib.service.ServiceContainer;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.lib.event.inventory.InventorySwitchEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Manage inventory configurations and player's inventory instances.
 *
 * This manager holds a collection of existing inventory configurations, that defines all inventories a player can switch to. Each player is mapped to
 * the name of his selected inventory configuration.
 */
public class InventoryInstanceManager {
    private static final String FOLDER_PATH = "plugins/Tesseract/inventories/";
    private static final String CONFIG_PATH = FOLDER_PATH + "config.json";
    private static final String PLAYERS_PATH = FOLDER_PATH + "players.yml";
    private static final Map<String, InventoryInstanceConfiguration> configurations = new HashMap<>();
    private static final Map<UUID, String> playerToConfig = new HashMap<>();

    static
    {
        configurations.put("default", new InventoryInstanceConfiguration("default", false, Collections.emptyList(), Collections.emptyMap(), null, Collections.emptyList(), GameMode.SURVIVAL));
    }

    /**
     * Load from file the inventory configurations and the selected configuration of each player.
     * Inventories configurations are loaded from {@value CONFIG_PATH}
     * Player's selected configurations are loaded from {@value PLAYERS_PATH}
     * It is not recommended calling this function after startup as it will not update inventories of connected players.
     */
    public static void loadConfigurations() throws IOException
    {
        File configFile = new File(CONFIG_PATH);
        File folder = new File(FOLDER_PATH);
        if (!configFile.exists())
        {
            if (!folder.exists())
                new File(FOLDER_PATH).mkdirs();
            return;
        }

        configurations.clear();
        InventoryInstanceConfiguration.load(configFile)
                                      .forEach(InventoryInstanceManager::addConfig);
        if (configurations.keySet().stream().noneMatch("default"::equals))
        {
            configurations.put("default", new InventoryInstanceConfiguration("default", false, Collections.emptyList(), Collections.emptyMap(), null, Collections.emptyList(), GameMode.SURVIVAL));
        }
    }

    public static void loadPlayers() {
        File playersFile = new File(PLAYERS_PATH);
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

    /**
     * Save inventory configurations and player's selected inventories mapping
     */
    public static void save() throws IOException
    {
        File playersFile = new File(FOLDER_PATH + "players.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        playerToConfig.forEach(((uuid, config) -> yaml.set(uuid.toString(), config)));
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
        EquipmentService equipmentService = ServiceContainer.get(EquipmentService.class);
        Collection<Invocable> invoked = equipmentService.getEquipment(player.getUniqueId()).getInvoked();
        for (Invocable invocable : invoked)
        {
            if (!invocable.isInvoked())
                continue;
            invocableSection.set(invocable.getUniqueName(), invocable.getHandSlot());
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
     * Load and apply a configuration to a player. The current player's inventory will be saved then replaced with the one specified by configName.
     * If the player has no inventory saved under that configuration, it will create a new inventory and give the configuration's default items.
     *
     * @throws IllegalArgumentException if the configuration does not exist.
     */
    public static void selectConfig(final Player player, final String configName)
    {
        if (!configurations.containsKey(configName))
            throw new IllegalArgumentException("This configuration does not exist");
        if (configName.equals(getSelectedConfig(player).getName()))
            return;

        InventorySwitchEvent event = new InventorySwitchEvent(player, getSelectedConfigName(player), configurations.get(configName));
        if (!event.callEvent())
            return;
        TesseractLib.logger().log(Level.INFO, "[Inventory] " + player.getName() + ": " + getSelectedConfigName(player) + " -> " + configName);

        save(player);
        applyConfig(player, event.getTo());

        try
        {
            save();
        }
        catch (IOException e)
        {
            TesseractLib.logger().log(Level.SEVERE, "failed to save inventories", e);
        }
    }

    static void applyConfig(final Player player, final String configName)
    {
        InventoryInstanceConfiguration config = configurations.get(configName);
        if (config != null)
            applyConfig(player, config);
    }

    /**
     * Apply a configuration. This does not save the player's current inventory.
     */
    static void applyConfig(final Player player, final InventoryInstanceConfiguration config)
    {
        final String configName = config.getName();
        if (configName.equals("default"))
            playerToConfig.remove(player.getUniqueId());
        else
            playerToConfig.put(player.getUniqueId(), configName);
        player.getInventory().clear();
        ServiceContainer.get(EquipmentService.class).uninvokeAll(player);

        File file = new File(FOLDER_PATH + configName + "/" + player.getUniqueId() + ".yml");
        if (file.exists())
        {
            applyExistingConfig(player, file, config);
        }
        else
        {
            applyNewConfig(player, config);
        }
    }

    private static void applyNewConfig(final Player player, final InventoryInstanceConfiguration config)
    {
        config.getItems().forEach(((material, integer) -> {
            ItemStack item = new ItemStack(material, integer);
            player.getInventory().addItem(item);
        }));
    }

    private static void applyExistingConfig(final Player player, final File file, final InventoryInstanceConfiguration config)
    {
        ConfigurationSection yaml = YamlConfiguration.loadConfiguration(file);

        // Load items
        var content = loadInventory(yaml, "items");
        player.getInventory().setContents(content);

        // Load invocables
        ConfigurationSection invocableSection = yaml.getConfigurationSection("invocables");
        if (invocableSection == null)
            return;
        EquipmentService equipmentService = ServiceContainer.get(EquipmentService.class);
        for (String invocableName : invocableSection.getKeys(false))
        {
            if (config.isRestrictInvocables() && !config.getInvocables().contains(invocableName))
                continue;
            int slot = invocableSection.getInt(invocableName);
            Invocable invocable = equipmentService.getEquipment(player.getUniqueId()).get(invocableName);
            if (invocable != null) {
                if (slot == -1)
                    equipmentService.invoke(player, invocable.getClass(), null, false);
                else
                    equipmentService.invoke(player, invocable.getClass(), slot, false);
            }
        }
    }

    private static ItemStack[] loadInventory(ConfigurationSection yaml, String inv) {
        ItemStack[] list = new ItemStack[41];
        if (yaml.contains(inv)) {
            int i = 0;
            for (Object item : Objects.requireNonNull(yaml.getList(inv))) {
                ItemStack itemStack = (ItemStack) item;
                if (itemStack != null)
                    list[i] = itemStack;
                i++;
            }
        }
        return list;
    }

    /**
     * Get the inventory configuration currently used by the player.
     *
     * @throws IllegalStateException if the selected configuration does not exist, probably because it has been removed while the player was offline.
     * This exception is not expected to be thrown as {@link InventoryInstanceEventHandler} handles this case on player connection.
     */
    public static InventoryInstanceConfiguration getSelectedConfig(final Player player)
    {
        if (playerToConfig.containsKey(player.getUniqueId()))
        {
            String configName = playerToConfig.get(player.getUniqueId());
            var config = configurations.get(configName);
            if (config == null)
                throw new IllegalStateException("Configuration " + configName + " is not defined.");
            return config;
        }
        return new InventoryInstanceConfiguration("default", false, Collections.emptyList(), Collections.emptyMap(), null, Collections.emptyList(), GameMode.SURVIVAL);
    }

    public static String getSelectedConfigName(final Player player)
    {
        return playerToConfig.getOrDefault(player.getUniqueId(), "default");
    }

    public static Collection<InventoryInstanceConfiguration> getAllConfigs()
    {
        return configurations.values();
    }

    public static void addConfig(InventoryInstanceConfiguration config)
    {
        if (config.getWorld() != null && configurations.values().stream().anyMatch(existing -> config.getWorld().equals(existing.getWorld())))
        {
            TesseractLib.logger().log(Level.WARNING, "Cannot register two inventories for the same world " + config.getWorld() + ", tried to register config " + config.getName());
            return;
        }
        configurations.put(config.getName(), config);
    }

    public static void removeConfig(final String configName) {
        configurations.remove(configName);
        playerToConfig.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(configName))
                .map(Map.Entry::getKey)
                .forEach(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null)
                        return;
                    String worldName = player.getWorld().getName();
                    String defaultConfig = getForWorld(worldName)
                            .map(InventoryInstanceConfiguration::getName)
                            .orElse("default");
                    selectConfig(player, defaultConfig);
                });
    }

    public static Optional<InventoryInstanceConfiguration> getForWorld(final String worldName)
    {
        return configurations.values()
                             .stream()
                             .filter(config -> worldName.equalsIgnoreCase(config.getWorld()))
                             .limit(1)
                             .findAny();
    }

    @Nullable
    public static InventoryInstanceConfiguration get(final String configName)
    {
        return configurations.get(configName);
    }
}

