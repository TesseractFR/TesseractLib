package onl.tesseract.tesseractlib.command;


import onl.tesseract.tesseractlib.menu.pet.PetTypeSelection;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FamilierCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args)
    {
        if(sender instanceof Player player)
            new PetTypeSelection(TPlayer.get(player)).open(player);
        return true;
    }

}
