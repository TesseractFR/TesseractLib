package onl.tesseract.lib.command.argument;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps;
import onl.tesseract.commandBuilder.CommandArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class OfflinePlayerArg extends CommandArgument<OfflinePlayer> {
    public OfflinePlayerArg(@NotNull final String name)
    {
        super(name);
    }

    @Override
    public void define(final CommandArgumentBuilderSteps.@NotNull Parser<OfflinePlayer> builder)
    {
        builder.parser((input, env) -> {
                   OfflinePlayer player = Bukkit.getOfflinePlayer(input);
                   if (!player.hasPlayedBefore())
                       throw new CommandArgumentException("Joueur introuvable");
                   return player;
               })
               .tabCompleter((input, env) -> null);
    }
}
