package onl.tesseract.tesseractlib.inventory;

import onl.tesseract.tesseractlib.event.PlayerInvocableInvokeEvent;
import onl.tesseract.tesseractlib.util.ChatFormats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

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

    @EventHandler
    public void onInvokeInvocable(final PlayerInvocableInvokeEvent event)
    {
        InventoryInstanceConfiguration config = InventoryInstanceManager.getSelectedConfig(event.getPlayer().getBukkitPlayer());
        if (!config.isRestrictInvocables())
            return;
        String invocableName = event.getInvocable().getLocalizedName();
        if (!config.getInvocables().contains(invocableName))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatFormats.EQUIPMENT_ERROR, "Vous ne pouvez pas invoquer cet équipement pour l'instant");
        }
    }

    @EventHandler
    public void onChangeWorld(final PlayerChangedWorldEvent event)
    {
        if (InventoryInstanceManager.getSelectedConfigName(event.getPlayer()).equals("admin"))
            return;
        final String from = event.getFrom().getName();
        final String to = event.getPlayer().getWorld().getName();

        final Optional<InventoryInstanceConfiguration> config = InventoryInstanceManager.getForWorld(to);
        if (config.isPresent())
        {
            InventoryInstanceManager.selectConfig(event.getPlayer(), config.get().getName());
        }
        else
        {
            InventoryInstanceManager.getForWorld(from)
                                    .ifPresentOrElse(any -> {
                                        if (from.equals(any.getWorld()))
                                            InventoryInstanceManager.selectConfig(event.getPlayer(), "default");
                                    }, () -> {
                                        var selectedConfig = InventoryInstanceManager.getSelectedConfig(event.getPlayer());
                                        if (selectedConfig.getAllowedWorlds().isEmpty())
                                            return;
                                        if (!selectedConfig.getAllowedWorlds().contains(to))
                                        {
                                            InventoryInstanceManager.selectConfig(event.getPlayer(), "default");
                                        }
                                    });
        }
    }
}
