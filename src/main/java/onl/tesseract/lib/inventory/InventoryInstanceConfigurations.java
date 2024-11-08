package onl.tesseract.lib.inventory;

import java.util.List;

public class InventoryInstanceConfigurations {
    private List<InventoryInstanceConfiguration> configs;

    public InventoryInstanceConfigurations()
    {
    }

    public InventoryInstanceConfigurations(final List<InventoryInstanceConfiguration> configs)
    {
        this.configs = configs;
    }

    public List<InventoryInstanceConfiguration> getConfigs()
    {
        return configs;
    }

    public void setConfigs(final List<InventoryInstanceConfiguration> configs)
    {
        this.configs = configs;
    }
}
