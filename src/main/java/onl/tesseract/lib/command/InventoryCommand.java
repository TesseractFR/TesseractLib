package onl.tesseract.lib.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.commandBuilder.CommandContext;
import onl.tesseract.commandBuilder.annotation.Argument;
import onl.tesseract.commandBuilder.annotation.Command;
import onl.tesseract.commandBuilder.annotation.Perm;
import onl.tesseract.lib.command.argument.InventoryInstanceConfigArg;
import onl.tesseract.lib.command.argument.PlayerArg;
import onl.tesseract.lib.command.argument.StringArg;
import onl.tesseract.lib.inventory.InventoryInstanceConfiguration;
import onl.tesseract.lib.inventory.InventoryInstanceConfigurationBuilder;
import onl.tesseract.lib.inventory.InventoryInstanceManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Command(playerOnly = true, permission = @Perm(mode = Perm.Mode.AUTO))
public class InventoryCommand extends CommandContext {

    @Command(description = "Sélectionner un inventaire")
    public void selectCommand(@Argument("instance") InventoryInstanceConfigArg instance,
                              @Argument(value = "player", optional = true) @Nullable PlayerArg player,
                              Player sender)
    {
        if (player == null)
            InventoryInstanceManager.selectConfig(sender, instance.get().getName());
        else
            InventoryInstanceManager.selectConfig(player.get(), instance.get().getName());
    }

    @Command(name = "config", description = "Configurer les différents inventaires")
    public static class Config {

        @Command
        public void create(@Argument("nom") StringArg name, CommandSender sender)
        {
            InventoryInstanceConfiguration config = new InventoryInstanceConfigurationBuilder()
                    .setName(name.get())
                    .build();
            InventoryInstanceManager.addConfig(config);
            sender.sendMessage(Component.text("Inventaire créé !", NamedTextColor.GREEN));
        }

        @Command
        public void remove(@Argument("instance") InventoryInstanceConfigArg instance)
        {
            InventoryInstanceManager.removeConfig(instance.get().getName());
        }
    }
}
