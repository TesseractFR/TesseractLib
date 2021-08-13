package onl.tesseract.tesseractlib.command.staff;

import net.kyori.adventure.text.Component;
import onl.tesseract.tesseractlib.cosmetics.Cosmetic;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.CosmeticType;
import onl.tesseract.tesseractlib.util.ChatFormats;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CosmeticCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] args)
    {

        if(!commandSender.hasPermission("cosmetic"))return false;
        if(args.length < 3){
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        UUID uuid = player.getUniqueId();
        CosmeticType type;
        Cosmetic cosmetic;
        try
        {
            type = CosmeticType.valueOf(args[2]);
        }catch (IllegalArgumentException e){
            commandSender.sendMessage(ChatFormats.COSMETICS_ERROR+ "Type de cosmetique inconnu.");
            return false;
        }
        try{
            cosmetic = CosmeticManager.stringToCosmetic(type, args[3]);
        }
        catch (IllegalArgumentException e)
        {
            commandSender.sendMessage(ChatFormats.COSMETICS_ERROR+ "Cosmetique inconnu.");
            return false;
        }


        switch (args[0])
        {
            case "give" -> {
                CosmeticManager.giveCosmetic(uuid, type, cosmetic);
                commandSender.sendMessage(ChatFormats.COSMETICS_SUCCESS
                                                  .append(Component.text("Tentative d'ajout effectuée.")));
                return true;
            }
            case "remove" -> {
                CosmeticManager.removeCosmetic(uuid, type, cosmetic);
                commandSender.sendMessage(ChatFormats.COSMETICS_SUCCESS
                        .append(Component.text("Tentative de retrait effectuée.")));
                return true;
            }
            default -> {
                commandSender.sendMessage(ChatFormats.COSMETICS_ERROR.append(Component.text("Commande inconnue.")));
                return true;
            }
        }
    }
}
