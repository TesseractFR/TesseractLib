package onl.tesseract.tesseractlib.command;


import net.kyori.adventure.text.Component;
import onl.tesseract.tesseractlib.familier.Pet;
import onl.tesseract.tesseractlib.familier.PetCategory;
import onl.tesseract.tesseractlib.familier.PetManager;
import onl.tesseract.tesseractlib.menu.pet.PetTypeSelection;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormat;
import onl.tesseract.tesseractlib.util.ChatFormats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        if (args.length == 0)
        {
            getHelp(sender);
            return true;
        }
        switch (args[0])
        {
            case "menu":
                new PetTypeSelection(TPlayer.get((Player) sender)).open((Player) sender);
                break;
            case "add":
                if (sender.hasPermission("pet.add"))
                {
                    if (args.length < 3)
                    {
                        sender.sendMessage(ChatFormats.PET_ERROR.append(Component.text("La commande est /familier add <player> <familier_Name>")));
                    }
                    else
                    {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null)
                            PetManager.ajouterFamilier(player, Pet.valueOf(args[2]), sender);
                        else
                            sender.sendMessage(ChatFormats.PET_ERROR.append(Component.text(" Le joueur " + args[1] + " n'est pas en ligne")));
                    }
                }
                else
                {
                    sender.sendMessage(ChatFormats.PET_ERROR.append(Component.text("Vous n'avez pas les droits d'utiliser cette commande")));
                }
                break;
            case "remove":
                if (sender.hasPermission("pet.remove"))
                {
                    if (args.length < 3)
                    {
                        sender.sendMessage(ChatFormats.PET_ERROR.append(Component.text("La commande est /familier remove <player> <familier_Name>")));
                    }
                    else
                    {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null)
                        {
                            PetManager.supprimerFamilier(player, Pet.valueOf(args[2]), sender);
                        }
                        else
                        {
                            sender.sendMessage(ChatFormats.PET_ERROR.append(Component.text(args[1] + " n'est pas online ")));
                        }
                    }
                }
                else
                {
                    sender.sendMessage(ChatFormats.PET_ERROR.append(Component.text("Vous n'avez pas les droits d'utiliser cette commande")));
                }
                break;
            case "list":
                if (sender.hasPermission("pet.list"))
                {
                    if (args.length < 3)
                    {
                        return false;
                    }
                    else
                    {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null)
                            PetManager.ListerFamilier(player, PetCategory.valueOf(args[2]), sender);

                        else
                        {
                            sender.sendMessage(ChatFormats.PET_ERROR.append(Component.text(args[1] + " n'est pas online ")));
                        }
                    }
                }
                else
                {
                    sender.sendMessage(ChatFormats.PET_ERROR.append(Component.text("Vous n'avez pas les droits d'utiliser cette commande")));
                }
                break;
            default:
                getHelp(sender);
                break;
        }

        return true;
    }

    public void getHelp(CommandSender sender)
    {
        sender.sendMessage(ChatColor.DARK_GREEN + "------------" + ChatColor.GREEN + " Commandes pour les familiers "
                                   + ChatColor.DARK_GREEN + "------------");
        sender.sendMessage(
                ChatColor.DARK_AQUA + "/familier menu : " + ChatColor.GREEN + "Affiche le menu des familiers");
        if (sender.hasPermission("pet.add"))
        {
            sender.sendMessage(ChatColor.DARK_AQUA + "/familier add <player> <familier_Name> : " + ChatColor.GREEN
                                       + "Ajoute le familier au joueur ");
        }
        if (sender.hasPermission("pet.remove"))
        {
            sender.sendMessage(ChatColor.DARK_AQUA + "/familier remove <player> <familier_Name> : " + ChatColor.GREEN
                                       + "Enleve le familier au joueur ");
        }
        if (sender.hasPermission("pet.list"))
        {
            sender.sendMessage(ChatColor.DARK_AQUA + "/familier list <player> <Type|all> : " + ChatColor.GREEN
                                       + "liste les familiers du joueur");
        }
        sender.sendMessage(ChatColor.DARK_GREEN + "-----------------------------------------------");
    }
}
