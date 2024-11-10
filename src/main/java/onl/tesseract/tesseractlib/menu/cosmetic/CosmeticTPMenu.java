package onl.tesseract.tesseractlib.menu.cosmetic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.TeleportationAnimation;
import onl.tesseract.tesseractlib.menu.boutique.global.GlobalBoutiqueMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.lib.menu.Button;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CosmeticTPMenu extends InventoryMenu {
    TPlayer tPlayer;

    public CosmeticTPMenu(TPlayer tPlayer)
    {
        super(27, ChatColor.LIGHT_PURPLE + "Particules de téléportation");
        this.tPlayer = tPlayer;
    }

    public CosmeticTPMenu(TPlayer tPlayer, InventoryMenu previous)
    {
        super(27, ChatColor.LIGHT_PURPLE + "Particules de téléportation", previous);
        this.tPlayer = tPlayer;
    }

    @Override
    public void open(Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        addBackButton();
        addQuitButton();

        int index = 1;
        for (TeleportationAnimation animation : TeleportationAnimation.values())
        {
            if (animation.equals(TeleportationAnimation.ROSETTE))
                continue;
            boolean hasAnimation = CosmeticManager.hasCosmetic(tPlayer.getUUID(),
                                                               TeleportationAnimation.getTypeName(), animation);
            if (animation == TeleportationAnimation.WATER && !hasAnimation)
            {
                hasAnimation = true;
                CosmeticManager.giveCosmetic(tPlayer.getUUID(), animation);
            }

            String lore = NEW_LINE + (hasAnimation ? ChatColor.GREEN + "Débloqué" : ChatColor.RED + "Bloqué") + NEW_LINE
                    + NEW_LINE;
            if (hasAnimation)
            {
                lore += "Cliquez pour utiliser le filtre " + animation.getName();
            }
            else
            {
                lore += "Cliquez pour acheter " + animation.getName() + NEW_LINE +
                        ChatColor.GRAY + "Coût : " + animation.getPrice() + " lys d'or" + NEW_LINE +
                        ChatColor.GRAY + "Vous avez : " + tPlayer.getMarketCurrency() + " lys d'or";
            }


            boolean finalHasAnimation = hasAnimation;
            addButton(index, animation.getIcon(), animation.getName(), lore,
                      tPlayer.getTp_animation() == animation, event -> {
                        if (finalHasAnimation)
                            tPlayer.setTp_animation(animation);
                        else
                        {
                            if (tPlayer.getMarketCurrency() >= animation.getPrice())
                                openConfirmationMenu(viewer, "Être vous sur de vouloir acheter", this,
                                                     event2 -> tPlayer.buyCosmetic(
                                                             animation,
                                                             animation.getPrice()));
                            else
                                tPlayer.sendMessage(ChatFormats.COSMETICS_ERROR.append(
                                        Component
                                                .text("Vous n'avez pas assez de lys d'or, cliquez ici pour en acheter")
                                                .hoverEvent(HoverEvent.showText(
                                                        Component
                                                                .text("Cliquez ici pour accéder à la boutique.",
                                                                      NamedTextColor.GOLD)))
                                                .clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl(
                                                        "https://tesseract.craftingstore.net/"))));
                        }
                        this.close();
                    });
            if (++index == 8)
                index = 10;
        }

        // VIP
        if (CosmeticManager.hasCosmetic(tPlayer.getBukkitPlayer().getUniqueId(), TeleportationAnimation.getTypeName(), TeleportationAnimation.ROSETTE))
        {
            addButton(22, new Button(new ItemBuilder(Material.END_ROD)
                                             .name("Rosace", NamedTextColor.LIGHT_PURPLE)
                                             .lore(new ItemLoreBuilder().newline()
                                                                        .append("Cliquez pour utiliser le filtre", TextColor.color(255, 177, 255))
                                                                        .get())
                                             .build()
                    , event -> {
                close();
                tPlayer.setTp_animation(TeleportationAnimation.ROSETTE);
                viewer.sendMessage(ChatFormats.COSMETICS_SUCCESS.append(Component.text("Votre animation de téléportation a été changé.")));
            }));
        }
        else
        {
            addButton(22, new Button(new ItemBuilder(Material.END_ROD)
                                     .name("Rosace", NamedTextColor.LIGHT_PURPLE)
                                     .lore(new ItemLoreBuilder().newline()
                                           .append("Exclusive au ", TextColor.color(255, 177, 255))
                                           .append("VIP", NamedTextColor.LIGHT_PURPLE)
                                           .get())
                                     .build()
                                     , event -> new GlobalBoutiqueMenu(tPlayer, this).open(viewer)));
        }
        super.open(viewer);
    }
}
