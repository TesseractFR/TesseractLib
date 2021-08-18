package onl.tesseract.tesseractlib.command;

import onl.tesseract.tesseractlib.menu.EquipmentMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EquipmentCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
    {
        if (sender instanceof Player player) {
            if (TPlayer.get(player).getEquipment()==null)return true;
            EquipmentMenu menu = new EquipmentMenu(TPlayer.get(player));
            menu.open(player);
        }

        return true;
    }
}