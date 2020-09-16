package onl.tesseract.tesseractlib.command;

import onl.tesseract.tesseractlib.menu.EquipmentMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class EquipmentCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            EquipmentMenu menu = new EquipmentMenu(TPlayer.get(player));
            menu.open(player);
        }

        return true;
    }
}