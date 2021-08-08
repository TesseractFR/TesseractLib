package onl.tesseract.tesseractlib.command;


import onl.tesseract.tesseractlib.familier.Pet;
import onl.tesseract.tesseractlib.familier.PetCategory;
import onl.tesseract.tesseractlib.familier.PetManager;
import onl.tesseract.tesseractlib.menu.pet.PetTypeSelection;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class FamilierCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label,
                             @Nonnull String[] args)
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
                        sender.sendMessage(ChatFormat.PET + ChatColor.RED
                                                   + "La commande est /familier add <player> <familier_Name>");
                    }
                    else
                    {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player != null)
                            PetManager.ajouterFamilier(player, Pet.valueOf(args[2]), sender);
                        else
                            sender.sendMessage(ChatFormat.PET + ChatColor.RED+ " Le joueur "+args[1]+" n'est pas en ligne");
                    }
                }
                else
                {
                    sender.sendMessage(
                            ChatFormat.PET + ChatColor.RED + "Vous n'avez pas les droits d'utiliser cette commande");
                }
                break;
            case "remove":
                if (sender.hasPermission("pet.remove"))
                {
                    if (args.length < 3)
                    {
                        sender.sendMessage(ChatFormat.PET + ChatColor.RED
                                                   + "La commande est /familier remove <player> <familier_Name>");
                    }
                    else
                    {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player!= null){
                        PetManager.supprimerFamilier(player, Pet.valueOf(args[2]), sender);}
                        else
                        {
                            sender.sendMessage(ChatFormat.PET + ChatColor.RED + player.getDisplayName() + " n'est pas online ");
                        }
                    }
                }
                else
                {
                    sender.sendMessage(
                            ChatFormat.PET + ChatColor.RED + "Vous n'avez pas les droits d'utiliser cette commande");
                }
                break;
            case "list":
                if (sender.hasPermission("pet.list"))
                {
                    if (args.length < 3)
                    {
                        sender.sendMessage(
                                ChatFormat.PET + ChatColor.RED
                                        + "La commande est /familier list <player> <Type|all>");
                    }
                    else
                    {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player != null)
                            PetManager.ListerFamilier(player, PetCategory.valueOf(args[2]), sender);

                        else
                        {
                            sender.sendMessage(ChatFormat.PET + ChatColor.RED + args[1] + " n'est pas online ");
                        }
                    }
                }
                else
                {
                    sender.sendMessage(
                            ChatFormat.PET + ChatColor.RED + "Vous n'avez pas les droits d'utiliser cette commande");
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
