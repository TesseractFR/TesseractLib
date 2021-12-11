package onl.tesseract.tesseractlib.command;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandBuilder;
import onl.tesseract.commandBuilder.OptionalCommandArgument;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceConfiguration;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceManager;
import org.bukkit.Bukkit;
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
    final CommandBuilder builder;

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
                        .withOptionalArg(new OptionalCommandArgument("player", Player.class)
                                .tabCompletion((sender, env) -> null)
                                .supplier((string, env) -> {
                                    Player player = Bukkit.getPlayer(string);
                                    if (player == null)
                                        throw new IllegalArgumentException();
                                    return player;
                                })
                                .error(IllegalArgumentException.class, "Joueur introuvable"))
                        .permission("inventory.select")
                        .description("Sélectionner un inventaire")
                        .command((sender, env) -> {
                            String invName = env.get("instance", String.class);
                            Player player = env.get("player", Player.class);
                            if (player == null)
                                player = (Player) sender;
                            InventoryInstanceManager.selectConfig(player, invName);
                        }))
                .subCommand(InventoryConfigCommand.get())
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
