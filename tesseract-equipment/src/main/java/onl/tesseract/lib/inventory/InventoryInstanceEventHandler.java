package onl.tesseract.lib.inventory;

import net.kyori.adventure.text.Component;
import onl.tesseract.lib.event.equipment.PlayerInvocableInvokeEvent;
import onl.tesseract.lib.event.inventory.InventorySwitchEvent;
import onl.tesseract.lib.util.ChatFormats;
import org.bukkit.GameMode;
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
        InventoryInstanceConfiguration config = InventoryInstanceManager.getSelectedConfig(event.getPlayer());
        if (!config.isRestrictInvocables())
            return;
        String invocableName = event.getInvocable().getUniqueName();
        if (!config.getInvocables().contains(invocableName))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatFormats.EQUIPMENT_ERROR.append(Component.text("Vous ne pouvez pas invoquer cet équipement pour l'instant")));
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

    @EventHandler
    public void onSwitch(InventorySwitchEvent event)
    {
        GameMode gameMode = event.getTo().getGameMode();
        event.getPlayer().setGameMode(gameMode);
    }
}
