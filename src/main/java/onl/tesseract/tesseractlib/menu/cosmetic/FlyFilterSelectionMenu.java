package onl.tesseract.tesseractlib.menu.cosmetic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.menu.cosmetic.CosmeticMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FlyFilterSelectionMenu extends InventoryMenu {
    TPlayer player;


    public FlyFilterSelectionMenu(TPlayer player, InventoryMenu previous)
    {
        super(27, ChatColor.BLUE + "Filtre de vol", previous==null ? new BoussoleMenu(player) : previous);
        this.player = player;
    }

    @Override
    public void open(Player viewer)
    {

        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (FlyFilter filter : FlyFilter.values())
        {
            if (filter != FlyFilter.NONE)
            {

                boolean hasTrail = CosmeticManager.hasCosmetic(player.getUUID(), FlyFilter.getTypeName(), filter);


                String lore = NEW_LINE + (hasTrail ? ChatColor.GREEN + "Débloqué" : ChatColor.RED + "Bloqué") + NEW_LINE
                        + NEW_LINE;
                if (hasTrail)
                {
                    lore += "Cliquez pour utiliser le filtre " + filter.getName();
                }
                else
                {
                    lore += "Cliquez pour acheter " + filter.getName() + NEW_LINE +
                            ChatColor.GRAY + "Coût : " + filter.getPrice() + " lys d'or" + NEW_LINE +
                            ChatColor.GRAY + "Vous avez : " + player.getMarketCurrency() + " lys d'or";
                }

                addButton(filter.getIndex(), filter.getMaterial(), filter.getName(), lore,
                          player.getFlyFilter() == filter, event -> {
                            if (hasTrail)
                                player.setFlyFilter(filter);
                            else
                            {
                                if (player.getMarketCurrency() >= filter.getPrice())
                                    openConfirmationMenu(viewer, "Être vous sur de vouloir acheter", this,
                                                         event2 -> player.buyCosmetic(
                                                                 FlyFilter.getTypeName(), filter, filter.getPrice()));
                                else
                                    player.sendMessage(ChatFormats.COSMETICS_ERROR.append(
                                            Component
                                                    .text("Vous n'avez pas assez de lys d'or, cliquez ici pour en acheter")
                                                    .hoverEvent(HoverEvent.showText(
                                                            Component
                                                                    .text("Cliquez ici pour accéder à la boutique.",
                                                                          NamedTextColor.GOLD)))
                                                    .clickEvent(ClickEvent.openUrl(
                                                            "https://tesseract.craftingstore.net/"))));
                            }
                            this.close();
                        });
            }
            else
            {
                addButton(22, filter.getMaterial(), filter.getName(), null, player.getFlyFilter() == filter,
                          event -> {
                              player.setFlyFilter(filter);
                              this.close();
                          });
            }
        }

        addBackButton();
        addQuitButton();
        super.open(viewer);
    }
}
