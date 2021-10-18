package onl.tesseract.tesseractlib.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Defines the configuration of an inventory a player can switch to.
 */
public class InventoryInstanceConfiguration {
    private String name;
    private boolean restrictInvocables;
    private Collection<String> invocables;
    private Map<Material, Integer> items;

    public InventoryInstanceConfiguration()
    {
    }

    public InventoryInstanceConfiguration(final String name, final boolean restrictInvocables, final Collection<String> invocables,
                                          final Map<Material, Integer> items)
    {
        this.name = name;
        this.restrictInvocables = restrictInvocables;
        this.invocables = invocables;
        this.items = items;
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
     * @return list of localized names. To match to {@link Invocable#getLocalizedName()}
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
}

