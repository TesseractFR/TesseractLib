package onl.tesseract.lib.command;

import onl.tesseract.lib.equipment.EquipmentMenu;
import onl.tesseract.lib.equipment.EquipmentService;
import onl.tesseract.lib.service.ServiceContainer;
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
            EquipmentMenu menu = new EquipmentMenu(player, ServiceContainer.get(EquipmentService.class), null);
            menu.open(player);
        }

        return true;
    }
}