package onl.tesseract.tesseractlib.menu.boutique;

import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.menu.boutique.global.GlobalBoutiqueMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BoutiqueMenu extends InventoryMenu {
    TPlayer player;
    public BoutiqueMenu(TPlayer player)
    {
        super(27, ChatColor.BLUE + "Boutique de Tesseract");
        this.player = player;
        this.previous = new BoussoleMenu(player);
    }

    @Override
    public void open(Player viewer) {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");

        addButton(13,Material.AMETHYST_CLUSTER,ChatColor.LIGHT_PURPLE+"Tous les serveurs",
                  ChatColor.GRAY+"Cliquez pour afficher les cosmetiques disponibles sur tout les serveurs.",
                  event->{new GlobalBoutiqueMenu(player,this).open(viewer);
                  });



        addBackButton();
        addQuitButton();
        super.open(viewer);
    }
}
