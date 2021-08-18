package onl.tesseract.tesseractlib.menu.boutique.global;

import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ElytraTrailboutiqueMenu extends InventoryMenu {

    TPlayer player;
    public ElytraTrailboutiqueMenu(TPlayer player, InventoryMenu previous)
    {
        super(27, "Boutique des sillages d'ailes", previous);
        this.player = player;
    }

    @Override
    public void open(Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (ElytraTrails trail : ElytraTrails.values())
        {
            if(trail.equals(ElytraTrails.NONE))continue;
            if(!CosmeticManager.hasCosmetic(player.getUUID(),ElytraTrails.getTypeName(),trail)){
                addButton(trail.getIndex(),trail.getMaterial(),trail.getName(),
                          "Cliquez pour acheter "+trail.getName()+NEW_LINE+
                        ChatColor.GRAY + "Coût : "+trail.getPrice()+" lys d'or"+NEW_LINE+
                        ChatColor.GRAY + "Vous avez : "+player.getMarketCurrency()+" lys d'or",event->{
                            CosmeticManager.tryToBuyEvent(viewer,this,player,ElytraTrails.getTypeName(),trail);
                        });
            }else {
                addInactiveButton(trail.getIndex(),Material.STRUCTURE_VOID,trail.getName(),ChatColor.GRAY+"Vous possedez "
                        + "déjà ce sillage");
            }
        }
        addBackButton();
        addQuitButton();
        super.open(viewer);
    }
}
