package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.bddfacade.VoteRepository;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.tesseractlib.util.menu.Button;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public abstract class AVoteRewardMenu extends InventoryMenu {
    private final TPlayer player;

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
                                           .append("Échanger des clés de votes contre des lys d'or", NamedTextColor.GRAY)
                                           .newline(2)
                                           .append("1 clés", NamedTextColor.YELLOW)
                                           .append(" = ", NamedTextColor.GRAY)
                                           .append(" 1 Lys d'or", NamedTextColor.YELLOW)
                                           .get())
                .build(), event -> {
            close();
            player.chatEntry(Component.text("Combien de clés de vote voulez-vous échanger ?"), 30, amountStr -> {
                try
                {
                    int amount = Integer.parseInt(((TextComponent) amountStr).content());
                    if (VoteRepository.getKeys(player.getUUID()) >= amount)
                    {
                        player.addMarketCurrency(amount);
                        VoteRepository.removeKeys(player.getUUID(), amount);
                        player.sendMessage(ChatFormats.VOTE, "Vous avez reçu " + amount + " Lys d'or !");
                    }
                    else
                    {
                        player.sendMessage(ChatFormats.CHAT_ERROR, "Vous n'avez pas suffisamment de lys d'or");
                    }
                }
                catch (NumberFormatException e)
                {
                    player.sendMessage(ChatFormats.CHAT_ERROR, "Nombre invalide");
                }
            });
        }));
    }
}

