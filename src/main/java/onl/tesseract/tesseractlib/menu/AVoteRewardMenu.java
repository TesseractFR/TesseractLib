package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.chat.ChatEntryService;
import onl.tesseract.lib.task.TaskScheduler;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.bddfacade.VoteRepository;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.tesseractlib.util.menu.Button;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.function.Consumer;

public abstract class AVoteRewardMenu extends InventoryMenu {
    protected final TPlayer player;

    protected AVoteRewardMenu(final TPlayer player, final int size, final InventoryMenu previous)
    {
        super(size, ChatColor.DARK_AQUA + "Récompenses", previous);
        this.player = player;
    }

    protected void addLysDorButton(final int index)
    {
        addButton(index, new Button(new ItemBuilder(Material.RAW_GOLD)
                .name("Lys d'or", NamedTextColor.GOLD)
                .lore(new ItemLoreBuilder().newline()
                                           .append("Échanger des points de vote contre des lys d'or", NamedTextColor.GRAY)
                                           .newline(2)
                                           .append("1 point", NamedTextColor.YELLOW)
                                           .append(" = ", NamedTextColor.GRAY)
                                           .append(" 1 Lys d'or", NamedTextColor.YELLOW)
                                           .get())
                .build(), event -> {
            askAmount(amount -> {
                player.addMarketCurrency(amount);
                player.sendMessage(ChatFormats.VOTE, "Vous avez reçu " + amount + " Lys d'or !");
            });
        }));
    }

    protected void askAmount(final Consumer<Integer> callback)
    {
        close();
        ChatEntryService chatEntryService = new ChatEntryService(new TaskScheduler(TesseractLib.instance, Bukkit.getScheduler()));
        chatEntryService.getChatEntry(player.getBukkitPlayer(), Component.text("Combien de points de vote voulez-vous échanger ?"), (amountStr) -> {
            try
            {
                int amount = Integer.parseInt(amountStr);
                if (amount <= 0)
                    throw new NumberFormatException();
                if (VoteRepository.getKeys(player.getUUID()) >= amount)
                {
                    callback.accept(amount);
                    VoteRepository.removePoints(player.getUUID(), amount);
                }
                else
                {
                    player.sendMessage(ChatFormats.CHAT_ERROR, "Vous n'avez pas suffisamment de points de vote");
                }
            }
            catch (NumberFormatException e)
            {
                player.sendMessage(ChatFormats.CHAT_ERROR, "Nombre invalide");
            }
            return null;
        });
    }

    protected boolean hasAmount(final int amount){
        return VoteRepository.getKeys(player.getUUID())>=amount;
    }

    protected void usePoints(final int amount,final Consumer<Integer> callback){
        close();
        if(!hasAmount(amount)){
            player.sendMessage(ChatFormats.CHAT_ERROR, "Vous n'avez pas suffisamment de points de vote");
            return;
        }
        VoteRepository.removePoints(player.getUUID(), amount);
        callback.accept(amount);
    }
}

