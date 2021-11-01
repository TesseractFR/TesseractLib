package onl.tesseract.tesseractlib.menu.boutique.global;

import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.familier.Pet;
import onl.tesseract.tesseractlib.cosmetics.familier.PetCategory;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PetBoutiqueMenu extends InventoryMenu {
    final TPlayer player;
    public PetBoutiqueMenu(TPlayer player, InventoryMenu previous)
    {
        super(54, "Boutique des sillages d'ailes", previous);
        this.player = player;
    }


    @Override
    public void open(Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        int i=0;

        for(PetCategory category : PetCategory.values()){
            for (Pet pet : category.getPets())
            {
                if(!CosmeticManager.hasCosmetic(player.getBukkitPlayer(), Pet.getTypeName(), pet)){
                    addButton(i++,pet.getHead(),pet.getName(),
                              "Cliquez pour acheter "+pet.getName()+NEW_LINE+
                                      ChatColor.GRAY + "Coût : "+pet.getPrice()+" lys d'or"+NEW_LINE+
                                      ChatColor.GRAY + "Vous avez : "+player.getMarketCurrency()+" lys d'or",
                              event-> CosmeticManager.tryToBuyEvent(viewer, this, player, Pet.getTypeName(), pet));
                }else {
                    addInactiveButton(i++,Material.STRUCTURE_VOID,pet.getName(),ChatColor.GRAY+"Vous possedez "
                            + "déjà ce famillié");
                }
            }
        }



        addBackButton();
        addQuitButton();
        super.open(viewer);
    }
}
