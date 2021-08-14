package onl.tesseract.tesseractlib.menu.boutique.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.CosmeticType;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.familier.Pet;
import onl.tesseract.tesseractlib.menu.boutique.BoutiqueMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GlobalBoutiqueMenu extends InventoryMenu {
    TPlayer player;

    public GlobalBoutiqueMenu(TPlayer player, InventoryMenu previous)
    {
        super(27, ChatColor.AQUA + "Boutique globale");
        this.player = player;
        this.previous = previous;
    }

    @Override
    public void open(Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");

        int totalPlayerTrail = CosmeticManager.getTotalPossessed(player.getUUID(), CosmeticType.ELYTRA_TRAIL);
        addButton(12, Material.ELYTRA,
                  ChatColor.LIGHT_PURPLE + "Sillages des ailes",
                  NEW_LINE +
                          ChatColor.GRAY + totalPlayerTrail + "/" + ElytraTrails.values().length +
                          " possédé" + (totalPlayerTrail > 2 ? "s" : "")
                          + NEW_LINE + NEW_LINE
                          + ChatColor.GRAY + "Customisez les particules de vos ailes", event -> {
                    new ElytraTrailboutiqueMenu(player, this).open(viewer);

                });
        int totalPlayerPet = CosmeticManager.getTotalPossessed(player.getUUID(), CosmeticType.PET);
        addButton(14, Material.LEAD,
                  ChatColor.BLUE + "Familier",
                  NEW_LINE +
                          ChatColor.GRAY + totalPlayerPet + "/" + Pet.values().length +
                          " possédé" + (totalPlayerPet > 2 ? "s" : "")
                          + NEW_LINE + NEW_LINE +
                          ChatColor.GRAY + "De petits familiers qui vous suivent partout", event -> {

                });

        addButton(22, Material.RAW_GOLD, ChatColor.GOLD + "Lys d'or",
                  NEW_LINE + ChatColor.GRAY + "Cliquez ici pour acheter des lys d'or", event -> {
                    player.sendMessage(Component.text(
                            ChatColor.GOLD
                                    + "[" + ChatColor.YELLOW + "Cliquez ici pour acheter des lys d'or" + ChatColor.GOLD
                                    +
                                    "]")
                                                .clickEvent(
                                                        ClickEvent.openUrl("https://tesseract.craftingstore.net/")));
                });
        super.addBackButton();
        super.addQuitButton();
        super.open(viewer);
    }
}
