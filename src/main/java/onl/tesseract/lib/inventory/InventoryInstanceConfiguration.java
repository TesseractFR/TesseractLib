package onl.tesseract.lib.inventory;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import onl.tesseract.lib.equipment.Invocable;
import org.bukkit.GameMode;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Defines the configuration of an inventory a player can switch to.
 */
public class InventoryInstanceConfiguration {
    private String name;
    private boolean restrictInvocables;
    private Collection<String> invocables = Collections.emptyList();
    private Map<Material, Integer> items = Collections.emptyMap();
    private String world;
    private Collection<String> allowedWorlds = Collections.emptyList();
    private GameMode gameMode = GameMode.SURVIVAL;

    public InventoryInstanceConfiguration()
    {
    }

    InventoryInstanceConfiguration(final String name, final boolean restrictInvocables, final Collection<String> invocables,
                                   final Map<Material, Integer> items, final String world, final Collection<String> allowedWorlds,
                                   final GameMode gameMode)
    {
        this.name = name;
        this.restrictInvocables = restrictInvocables;
        this.invocables = invocables;
        this.items = items;
        this.world = world;
        this.allowedWorlds = allowedWorlds;
        this.gameMode = gameMode;
    }

    public static Collection<InventoryInstanceConfiguration> load(final File file) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, InventoryInstanceConfigurations.class).getConfigs();
    }

    public String getName()
    {
        return name;
    }

    public boolean isRestrictInvocables()
    {
        return restrictInvocables;
    }

    /**
     * If {@link InventoryInstanceConfiguration#isRestrictInvocables()} is true, this returns the list of allowed invocables
     *
     * @return list of localized names. To match to {@link Invocable#getUniqueName()}
     */
    public Collection<String> getInvocables()
    {
        return invocables;
    }

    /**
     * Default items given to the player the first time he uses this inventory
     *
     * @return A map of item (material to quantity)
     */
    public Map<Material, Integer> getItems()
    {
        return items;
    }

    public String getWorld()
    {
        return world;
    }

    public Collection<String> getAllowedWorlds()
    {
        return allowedWorlds;
    }

    @JsonProperty("gameMode")
    @JsonAlias("gamemode")
    public GameMode getGameMode()
    {
        return gameMode;
    }
}

