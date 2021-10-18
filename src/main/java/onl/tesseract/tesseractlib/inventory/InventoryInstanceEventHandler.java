package onl.tesseract.tesseractlib.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class InventoryInstanceEventHandler implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event)
    {
        String configName = InventoryInstanceManager.getSelectedConfigName(event.getPlayer());
        if (InventoryInstanceManager.getAllConfigs().stream().noneMatch(config -> config.getName().equals(configName)))
        {
            InventoryInstanceManager.applyConfig(event.getPlayer(), "default");
        }
    }
}
