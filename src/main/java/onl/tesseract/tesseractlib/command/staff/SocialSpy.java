package onl.tesseract.tesseractlib.command.staff;

import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class SocialSpy implements CommandExecutor {
    /**
     * Set of players who are spying
     */
    static public Set<TPlayer> spies = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (! (sender instanceof Player)) return false;

        TPlayer tPlayer = TPlayer.get((Player) sender);
        if (spies.contains(tPlayer)) {
            spies.remove(tPlayer);
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Social spy désactivé");
        }
        else {
            spies.add(tPlayer);
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Social spy activé");
        }

        return true;
    }
}
