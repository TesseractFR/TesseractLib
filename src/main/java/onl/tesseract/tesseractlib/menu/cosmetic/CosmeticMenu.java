package onl.tesseract.tesseractlib.menu.cosmetic;

import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.cosmetics.familier.Pet;
import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.menu.cosmetic.pet.PetTypeSelection;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CosmeticMenu extends InventoryMenu {
    final TPlayer player;


    public CosmeticMenu(TPlayer player,InventoryMenu previous)
    {
        super(27,ChatColor.AQUA + "Menu des cosmetiques",previous==null ?new BoussoleMenu(player) : previous);
        this.player = player;
    }

    @Override
    public void open(Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");

        int totalPlayerTrail = CosmeticManager.getTotalPossessed(player.getUUID(), ElytraTrails.getTypeName());
        addButton(10, Material.ELYTRA,
                  ChatColor.LIGHT_PURPLE + "Sillages des ailes",
                  NEW_LINE +
                          ChatColor.GRAY + totalPlayerTrail + "/" + ElytraTrails.values().length +
                          " possédé" + (totalPlayerTrail > 2 ? "s" : "")
                          + NEW_LINE + NEW_LINE
                          + ChatColor.GRAY + "Customisez les particules de vos ailes", event -> new ElytraTrailSelectionMenu(player, this).open(viewer));
        int totalPlayerFlyFilter = CosmeticManager.getTotalPossessed(player.getUUID(), FlyFilter.getTypeName());
        addButton(12, Material.BLAZE_POWDER,
                  ChatColor.DARK_GREEN + "Filtre de vol & jetpack",
                  NEW_LINE +
                          ChatColor.GRAY + totalPlayerFlyFilter + "/" + FlyFilter.values().length +
                          " possédé" + (totalPlayerFlyFilter > 2 ? "s" : "")
                          + NEW_LINE + NEW_LINE +
                          ChatColor.GRAY + "Des filtres qui apparaissent lorsque vous voler en Créatif ou lors de "
                          + "l'utilisation du jetpack en Semi-RP",
                  event -> new FlyFilterSelectionMenu(player, this).open(viewer));


        int totalPlayerPet = CosmeticManager.getTotalPossessed(player.getUUID(), Pet.getTypeName());
        addButton(14, Material.LEAD,
                  ChatColor.BLUE + "Familier",
                  NEW_LINE +
                          ChatColor.GRAY + totalPlayerPet + "/" + Pet.values().length +
                          " possédé" + (totalPlayerPet > 2 ? "s" : "")
                          + NEW_LINE + NEW_LINE +
                          ChatColor.GRAY + "De petits familiers qui vous suivent partout", event -> new PetTypeSelection(player, this).open(viewer));
        addButton(16, Material.ENDER_PEARL, ChatColor.BLUE + "Téléportations",
                NEW_LINE + ChatColor.GRAY + "Customisez les particules de téléportation",
                event -> {
                    new CosmeticTPMenu(player, this).open(viewer);
                });

        super.addBackButton();
        super.addQuitButton();
        super.open(viewer);
    }
}
