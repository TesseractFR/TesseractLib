package onl.tesseract.tesseractlib.command;

import onl.tesseract.tesseractlib.menu.VoteMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VoteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label,
                             final @NotNull String[] args)
    {
        if (!(sender instanceof Player player))
            return false;

        TPlayer tPlayer = TPlayer.get(player);
        new VoteMenu(tPlayer).open(player);
        return true;
    }
}
