package onl.tesseract.tesseractlib.menu.boutique.global;

import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FlyFilterBoutiqueMenu extends InventoryMenu {
    TPlayer player;
    public FlyFilterBoutiqueMenu(TPlayer player, InventoryMenu previous)
    {
        super(54, "Boutique des filtres de vol", previous);
        this.player = player;
    }

    @Override
    public void open(Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (FlyFilter filter : FlyFilter.values())
        {
            if(filter.equals(FlyFilter.NONE))continue;
            if(!CosmeticManager.hasCosmetic(player.getUUID(), FlyFilter.getTypeName(), filter)){
                addButton(filter.getIndex(),filter.getMaterial(),filter.getName(),
                          "Cliquez pour acheter "+filter.getName()+NEW_LINE+
                                  ChatColor.GRAY + "Coût : "+filter.getPrice()+" lys d'or"+NEW_LINE+
                                  ChatColor.GRAY + "Vous avez : "+player.getMarketCurrency()+" lys d'or",event->{
                            CosmeticManager.tryToBuyEvent(viewer,this,player,ElytraTrails.getTypeName(),filter);
                        });
            }else {
                addInactiveButton(filter.getIndex(),Material.STRUCTURE_VOID,filter.getName(),ChatColor.GRAY+"Vous possedez "
                        + "déjà ce sillage");
            }
        }
        addBackButton();
        addQuitButton();
        super.open(viewer);
    }
}
