package onl.tesseract.tesseractlib.menu;

import onl.tesseract.tesseractlib.player.TPlayer;
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

        addLysDorButton(4);

        super.open(viewer);
    }
}
