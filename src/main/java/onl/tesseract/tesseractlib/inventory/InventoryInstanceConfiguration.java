package onl.tesseract.tesseractlib.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

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

    public Collection<String> getInvocables()
    {
        return invocables;
    }

    public Map<Material, Integer> getItems()
    {
        return items;
    }
}

