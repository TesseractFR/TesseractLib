package onl.tesseract.tesseractlib.command.argument;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps;
import onl.tesseract.commandBuilder.CommandArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerArg extends CommandArgument<Player> {
    public PlayerArg(@NotNull final String name)
    {
        super(name);
    }

    @Override
    public void define(final CommandArgumentBuilderSteps.@NotNull Parser<Player> builder)
    {
        builder.parser((input, env) -> {
                   Player player = Bukkit.getPlayer(input);
                   if (player == null)
                       throw new CommandArgumentException("Joueur introuvable");
                   return player;
               })
               .tabCompleter((input, env) -> null);
    }
}
