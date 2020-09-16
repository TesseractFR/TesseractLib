package onl.tesseract.tesseractlib.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public class ReplyToMsg implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args)
    {
        if (args.length == 0)
            return false;

        CommandSender receiver = MsgCommand.getReplyTo(sender);
        if (receiver != null)
        {
            // Get the message in one string
            StringBuilder message = new StringBuilder();
            for (String arg : args) message.append(arg).append(" ");

            // Reply
            MsgCommand.sendMessage(sender, receiver, message.toString());
        }
        else
            sender.sendMessage(ChatColor.RED + "Il n'y a personne à qui répondre.");
        return true;
    }
}
