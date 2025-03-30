package onl.tesseract.lib.command.argument;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps;
import onl.tesseract.commandBuilder.CommandArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OfflineUUIDPlayerArg extends CommandArgument<OfflinePlayer> {
    public OfflineUUIDPlayerArg(@NotNull final String name) {
        super(name);
    }

    @Override
    public void define(final CommandArgumentBuilderSteps.@NotNull Parser<OfflinePlayer> builder) {
        builder.parser((input, env) -> {
                    try {
                        UUID uuid = UUID.fromString(input);
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        if (!offlinePlayer.hasPlayedBefore())
                            throw new CommandArgumentException("Joueur introuvable");
                        return offlinePlayer;
                    } catch (Exception e) {
                        Player player = Bukkit.getPlayer(input);
                        if (player == null)
                            throw new CommandArgumentException("Joueur introuvable");
                        return player;
                    }
                })
                .tabCompleter((input, env) -> null);
    }
}
