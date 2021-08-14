package onl.tesseract.tesseractlib.command;

import onl.tesseract.tesseractlib.menu.boutique.BoutiqueMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BoutiqueCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings)
    {
        if(commandSender instanceof Player player)
            new BoutiqueMenu(TPlayer.get(player)).open(player);
        return true;
    }

}
