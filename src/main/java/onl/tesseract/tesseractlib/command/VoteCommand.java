package onl.tesseract.tesseractlib.command;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.menu.VoteMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class VoteCommand implements CommandExecutor {

    private static Class<? extends VoteMenu> voteMenuClass = VoteMenu.class;

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label,
                             final @NotNull String[] args)
    {
        if (!(sender instanceof Player player))
            return false;

        TPlayer tPlayer = TPlayer.get(player);
        VoteMenu voteMenu;
        try
        {
            Constructor<? extends VoteMenu> declaredConstructor = voteMenuClass.getDeclaredConstructor(TPlayer.class);
            voteMenu = declaredConstructor.newInstance(tPlayer);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to instantiate vote menu", e);
            sender.sendMessage(ChatColor.RED + "Une erreur inattendue est survenue. Veuillez contactez un administrateur.");
            return true;
        }
        voteMenu.open(player);
        return true;
    }

    public static void setVoteMenuClass(final Class<? extends VoteMenu> voteMenuClass)
    {
        VoteCommand.voteMenuClass = voteMenuClass;
    }
}
