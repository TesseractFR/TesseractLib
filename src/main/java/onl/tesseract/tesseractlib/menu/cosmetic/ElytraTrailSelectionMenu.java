package onl.tesseract.tesseractlib.menu.cosmetic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ElytraTrailSelectionMenu extends InventoryMenu {
    final TPlayer player;


    public ElytraTrailSelectionMenu(TPlayer player, InventoryMenu previous)
    {
        super(27, ChatColor.BLUE + "Sillages d'ailes", previous == null ? new BoussoleMenu(player) : previous);
        this.player = player;
    }

    @Override
    public void open(Player viewer)
    {
        // For each existing trails
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (ElytraTrails trail : ElytraTrails.values())
        {
            if (trail != ElytraTrails.NONE)
            {
                boolean hasTrail = CosmeticManager.hasCosmetic(player.getBukkitPlayer(), ElytraTrails.getTypeName(), trail);


                String lore = NEW_LINE + (hasTrail ? ChatColor.GREEN + "Débloqué" : ChatColor.RED + "Bloqué") + NEW_LINE
                        + NEW_LINE;
                if (hasTrail)
                {
                    lore += "Cliquez pour utiliser le sillage " + trail.getName();
                }
                else
                {
                    lore += "Cliquez pour acheter " + trail.getName() + NEW_LINE +
                            ChatColor.GRAY + "Coût : " + trail.getPrice() + " lys d'or" + NEW_LINE +
                            ChatColor.GRAY + "Vous avez : " + player.getMarketCurrency() + " lys d'or";
                }

                addButton(trail.getIndex(), trail.getMaterial(), trail.getName(), lore,
                        player.getActiveTrail() == trail, event -> {
                            if (hasTrail){
                                player.setActiveTrail(trail);
                                this.close();
                            }
                            else
                            {
                                if (player.getMarketCurrency() >= trail.getPrice())
                                    openConfirmationMenu(viewer, "Être vous sur de vouloir acheter", this,
                                            event2 -> {
                                                player.buyCosmetic(
                                                        trail, trail.getPrice());

                                                this.close();
                                            });
                                else
                                {
                                    player.sendMessage(ChatFormats.COSMETICS_ERROR.append(
                                            Component
                                                    .text("Vous n'avez pas assez de lys d'or, cliquez ici pour en acheter")
                                                    .hoverEvent(HoverEvent.showText(
                                                            Component
                                                                    .text("Cliquez ici pour accéder à la boutique.",
                                                                            NamedTextColor.GOLD)))
                                                    .clickEvent(ClickEvent.openUrl(
                                                            "https://tesseract.craftingstore.net/"))));

                                    this.close();
                                }
                            }
                        });
            }
            else
            {
                addButton(22, trail.getMaterial(), trail.getName(), null, player.getActiveTrail() == trail,
                        event -> {
                            player.setActiveTrail(trail);
                            this.close();
                        });
            }
        }

        addBackButton();
        addQuitButton();
        super.open(viewer);
    }
}
