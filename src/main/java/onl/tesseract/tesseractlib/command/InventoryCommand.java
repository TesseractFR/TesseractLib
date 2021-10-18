package onl.tesseract.tesseractlib.command;

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
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label,
                             final @NotNull String[] args)
    {
        if (!(sender instanceof Player player) || args.length < 2)
            return false;

        if (args[0].equals("select"))
        {
            String name = args[1];
            InventoryInstanceManager.selectConfig(player, name);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias,
                                                final @NotNull String[] args)
    {
        if (args.length == 1)
            return List.of("select");
        if (args.length == 2)
        {
            List<String> configs = InventoryInstanceManager.getAllConfigs()
                                                           .stream()
                                                           .map(InventoryInstanceConfiguration::getName)
                                                           .collect(Collectors.toList());
            configs.add("default");
            return configs;
        }
        return null;
    }
}
