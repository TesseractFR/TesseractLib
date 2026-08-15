package onl.tesseract.lib.inventory;

import org.bukkit.GameMode;
import org.bukkit.Material;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InventoryInstanceConfigurationBuilder {
    private String name;
    private boolean restrictInvocables;
    private Collection<String> invocables = Collections.emptyList();
    private Map<Material, Integer> items = Collections.emptyMap();
    private String world;
    private Collection<String> allowedWorlds = Collections.emptyList();
    private GameMode gameMode = GameMode.SURVIVAL;

    public InventoryInstanceConfigurationBuilder setName(final String name)
    {
        this.name = name;
        return this;
    }

    public InventoryInstanceConfigurationBuilder setRestrictInvocables(final boolean restrictInvocables)
    {
        this.restrictInvocables = restrictInvocables;
        return this;
    }

    public InventoryInstanceConfigurationBuilder setInvocables(final Collection<String> invocables)
    {
        this.invocables = invocables;
        return this;
    }

    public InventoryInstanceConfigurationBuilder setItems(final Map<Material, Integer> items)
    {
        this.items = items;
        return this;
    }

    public InventoryInstanceConfigurationBuilder setWorld(final String world)
    {
        this.world = world;
        return this;
    }

    public InventoryInstanceConfigurationBuilder setAllowedWorlds(final Collection<String> allowedWorlds)
    {
        this.allowedWorlds = allowedWorlds;
        return this;
    }

    public InventoryInstanceConfigurationBuilder setGameMode(final GameMode gameMode)
    {
        this.gameMode = gameMode;
        return this;
    }

    public InventoryInstanceConfiguration build()
    {
        return new InventoryInstanceConfiguration(name, restrictInvocables, invocables, items, world, allowedWorlds, gameMode);
    }
}