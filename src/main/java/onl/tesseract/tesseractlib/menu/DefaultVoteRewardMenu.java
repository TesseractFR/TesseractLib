package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DefaultVoteRewardMenu extends AVoteRewardMenu {
    public DefaultVoteRewardMenu(final TPlayer player, final InventoryMenu previous)
    {
        super(player, 9, previous);
    }

    @Override
    public void open(final Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        addBackButton();
        addQuitButton();

        addNoRewardButton(4);

        super.open(viewer);
    }

    void addNoRewardButton(final int index)
    {
        add(index, new ItemBuilder(Material.STRUCTURE_VOID)
                .name("Récompense", NamedTextColor.GOLD)
                .lore(new ItemLoreBuilder()
                        .newline()
                        .append("Il n'y a pour l'instant aucune récompense disponible pour ce serveur. Pas de panique ! Tes points de vote peuvent "
                                + "être utilisés sur les serveurs suivants :").newline()
                        .append("→ SemiRP")
                        .get())
                .build());
    }
}
