package onl.tesseract.tesseractlib.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class InventoryInstanceConfiguration {
    private String name;
    private boolean restrictInvocables;
    private Collection<String> names;

    public InventoryInstanceConfiguration()
    {
    }

    public InventoryInstanceConfiguration(final String name, final boolean restrictInvocables, final Collection<String> names)
    {
        this.name = name;
        this.restrictInvocables = restrictInvocables;
        this.names = names;
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

    public Collection<String> getNames()
    {
        return names;
    }
}

