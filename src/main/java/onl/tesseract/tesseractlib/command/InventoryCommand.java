package onl.tesseract.tesseractlib.command;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandBuilder;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceConfiguration;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryCommand implements CommandExecutor, TabCompleter {
    CommandBuilder builder;

    public InventoryCommand()
    {
        this.builder = new CommandBuilder("inventory")
                .playerOnly(true)
                .subCommand(new CommandBuilder("select")
                        .withArg(new CommandArgument("instance", String.class)
                                .tabCompletion((sender, env) -> InventoryInstanceManager.getAllConfigs()
                                                                                        .stream()
                                                                                        .map(InventoryInstanceConfiguration::getName)
                                                                                        .collect(Collectors.toList()))
                                .supplier((string, env) -> string))
                        .permission("inventory.select")
                        .description("Sélectionner un inventaire")
                        .command((sender, env) -> {
                            String invName = env.get("instance", String.class);
                            Player player = (Player) sender;
                            InventoryInstanceManager.selectConfig(player, invName);
                        }))
                .subCommand(new InventoryConfigCommand("config"))
                .command(((sender, commandEnvironment) -> {}));
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label,
                             final @NotNull String[] args)
    {
        builder.execute(sender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias,
                                                final @NotNull String[] args)
    {
        return builder.tabComplete(sender, args);
    }
}
