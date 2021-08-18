package onl.tesseract.tesseractlib.menu.boutique.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.cosmetics.familier.Pet;
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

        int totalPlayerTrail = CosmeticManager.getTotalPossessed(player.getUUID(), ElytraTrails.getTypeName());
        addButton(11, Material.ELYTRA,
                  ChatColor.LIGHT_PURPLE + "Sillages des ailes",
                  NEW_LINE +
                          ChatColor.GRAY + totalPlayerTrail + "/" + ElytraTrails.values().length +
                          " possédé" + (totalPlayerTrail > 2 ? "s" : "")
                          + NEW_LINE + NEW_LINE
                          + ChatColor.GRAY + "Customisez les particules de vos ailes", event -> {
                    new ElytraTrailboutiqueMenu(player, this).open(viewer);

                });
        int totalPlayerFlyFilter = CosmeticManager.getTotalPossessed(player.getUUID(),FlyFilter.getTypeName());
        addButton(13, Material.BLAZE_POWDER,
                  ChatColor.DARK_GREEN + "Filtre de vol & jetpack",
                  NEW_LINE +
                          ChatColor.GRAY + totalPlayerFlyFilter + "/" + FlyFilter.values().length +
                          " possédé" + (totalPlayerFlyFilter > 2 ? "s" : "")
                          + NEW_LINE + NEW_LINE +
                          ChatColor.GRAY + "Des filtres qui apparaissent lorsque vous voler en Créatif ou lors de "
                          + "l'utilisation du jetpack en Semi-RP",
                  event -> {
                    new FlyFilterBoutiqueMenu(player,this).open(viewer);
                });


        int totalPlayerPet = CosmeticManager.getTotalPossessed(player.getUUID(), Pet.getTypeName());
        addButton(15, Material.LEAD,
                  ChatColor.BLUE + "Familier",
                  NEW_LINE +
                          ChatColor.GRAY + totalPlayerPet + "/" + Pet.values().length +
                          " possédé" + (totalPlayerPet > 2 ? "s" : "")
                          + NEW_LINE + NEW_LINE +
                          ChatColor.GRAY + "De petits familiers qui vous suivent partout", event -> {
                        new PetBoutiqueMenu(player,this).open(viewer);
                });

        addButton(22, Material.RAW_GOLD, ChatColor.GOLD + "Lys d'or",
                  NEW_LINE+ ChatColor.GRAY + "Vous avez "+ChatColor.DARK_AQUA+player.getMarketCurrency()+ChatColor.GRAY+
                          " lys d'or."+
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
