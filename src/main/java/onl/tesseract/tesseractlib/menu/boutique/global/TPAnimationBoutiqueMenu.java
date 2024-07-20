package onl.tesseract.tesseractlib.menu.boutique.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.CosmeticPreview;
import onl.tesseract.tesseractlib.cosmetics.TeleportationAnimation;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.tesseractlib.util.menu.Button;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TPAnimationBoutiqueMenu extends InventoryMenu {
    TPlayer player;

    public TPAnimationBoutiqueMenu(TPlayer player, InventoryMenu previous)
    {
        super(27, "Boutique des animation de téléportation", previous);
        this.player = player;
    }


    @Override
    public void open(Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        int index = 1;
        for (TeleportationAnimation animation : TeleportationAnimation.values())
        {
            if (animation.equals(TeleportationAnimation.WATER) || animation.equals(TeleportationAnimation.ROSETTE))
                continue;
            if (!CosmeticManager.hasCosmetic(player.getBukkitPlayer(), TeleportationAnimation.getTypeName(), animation))
            {

                final Component previewString = CosmeticPreview.hasPreviewed(viewer.getUniqueId(), animation)
                                             ? Component.text("Déjà prévisualisé", NamedTextColor.RED)
                                             : Component.text("prévisualiser", NamedTextColor.GRAY);
                addButton(index, new Button(new ItemBuilder(animation.getIcon())
                        .name(animation.getName(), NamedTextColor.GOLD)
                        .lore(new ItemLoreBuilder()
                                .newline()
                                .append("Clic gauche : ", NamedTextColor.GRAY)
                                .append("Acheter", NamedTextColor.YELLOW)
                                .newline()
                                .append("Clic droit : ", NamedTextColor.GRAY)
                                .append(previewString)
                                .newline(2)
                                .append("Coût : ", NamedTextColor.GRAY)
                                .append(animation.getPrice() + " lys d'or", NamedTextColor.YELLOW)
                                .newline()
                                .append("Vous avez : ", NamedTextColor.GRAY)
                                .append(player.getMarketCurrency() + " lys d'or", NamedTextColor.YELLOW)
                                .get())
                        .build(), event -> {
                    if (event.isRightClick() && !CosmeticPreview.hasPreviewed(viewer.getUniqueId(), animation))
                    {
                        close();
                        animation.animate(viewer.getLocation(), 3*20);
                        CosmeticPreview.setPreview(viewer.getUniqueId(), animation);
                    }
                    else if (event.isLeftClick())
                    {
                        CosmeticManager.tryToBuyEvent(viewer, this, player, animation);
                    }
                }));
            }
            else
            {
                addInactiveButton(index, Material.STRUCTURE_VOID, animation.getName(), ChatColor.GRAY + "Vous possedez "
                        + "déjà cet effet de téléportation");
            }
            if (++index == 8)
                index = 10;
            if (index == 13)
                index++;
        }
        addBackButton();
        addQuitButton();
        super.open(viewer);
    }
}
