package onl.tesseract.tesseractlib.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import onl.tesseract.tesseractlib.command.staff.SocialSpy;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class MsgCommand implements CommandExecutor {
    /**
     * Maps a receiver to its last sender.
     */
    static Map<CommandSender, CommandSender> messages = new HashMap<>();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args)
    {
        if (args.length < 2)
            return false;

        Player other = Bukkit.getPlayerExact(args[0]);
        if (other != null)
        {
            // Get the message in one string
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                message.append(args[i]).append(" ");

            // Send
            sendMessage(sender, other, message.toString(), true);
        }
        else
            sender.sendMessage(ChatColor.RED + "Joueur introuvable");

        return true;
    }

    public static void sendMessage(CommandSender sender, CommandSender receiver, String message , boolean etat)
    {
        if (sender.equals(receiver)) return;
        if (receiver instanceof Player && !((Player) receiver).isOnline())
        {
            sender.sendMessage(ChatColor.RED + receiver.getName() + " s'est déconnecté.");
            messages.remove(sender);
            messages.remove(receiver);
            return;
        }
        messages.put(receiver, sender);

        // Send the message to the receiver
        receiver.sendMessage(new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.ITALIC + "Reçu de " + ChatColor.RED + sender.getName() + " » ")
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + sender.getName() + " "))
                .append(ChatColor.AQUA + "" + ChatColor.ITALIC + message)
                .create()
        );
        if (etat)
        {
            Bukkit.getPlayerExact(receiver.getName()).playSound(Bukkit.getPlayerExact(receiver.getName()).getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
        }

        // Send feedback
        sender.sendMessage(new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.ITALIC + "Envoyé à " + ChatColor.RED + receiver.getName() + " » ")
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + receiver.getName() + " "))
                .append(ChatColor.GRAY + "" + ChatColor.ITALIC + message)
                .create()
        );

        // Send to social spies
        SocialSpy.spies.forEach(spy -> {
            if (spy.isOnline() && !spy.getBukkitPlayer().equals(sender) && ! spy.getBukkitPlayer().equals(receiver))
            {
                spy.sendMessage(new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.ITALIC + "Message de " + ChatColor.RED + sender.getName() + ChatColor.GRAY + ChatColor.ITALIC + " envoyé à " + ChatColor.RED + receiver.getName() + " » ")
                        .append(ChatColor.GRAY + "" + ChatColor.ITALIC + message)
                        .create()
                );
            }
        });
    }

    public static CommandSender getReplyTo(CommandSender sender)
    {
        if (messages.containsKey(sender))
            return messages.get(sender);

        else if (messages.containsValue(sender))
            for (CommandSender receiver : messages.keySet())
                if (messages.get(receiver).getName().equals(sender.getName()))
                    return receiver;
        return null;
    }
}
