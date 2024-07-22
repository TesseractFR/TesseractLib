package onl.tesseract.tesseractlib.menu.boutique;

import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.menu.boutique.global.GlobalBoutiqueMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class BoutiqueMenu extends onl.tesseract.tesseractlib.util.menu.BoutiqueMenu {
    public BoutiqueMenu(TPlayer player) {
        super(player, 27, "Boutique de Tesseract", NamedTextColor.BLUE, new BoussoleMenu(player));
    }

    @Override
    public void open(Player viewer) {
        addButton(13, Material.AMETHYST_CLUSTER, "Tous les serveurs", NamedTextColor.LIGHT_PURPLE,
                "Cliquez pour afficher les cosmetiques disponibles sur tout les serveurs.", GRAY,
                event -> new GlobalBoutiqueMenu(tPlayer, this).open(viewer));
        super.open(viewer);
    }
}
