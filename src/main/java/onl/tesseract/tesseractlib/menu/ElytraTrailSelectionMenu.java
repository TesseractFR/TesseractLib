package onl.tesseract.tesseractlib.menu;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.CosmeticType;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ElytraTrailSelectionMenu extends InventoryMenu {
    TPlayer player;


    public ElytraTrailSelectionMenu(TPlayer player)
    {
        super(36, ChatColor.BLUE + "Sillages d'ailes", new BoussoleMenu(player));
        this.player = player;
    }

    @Override
    public void open(Player viewer)
    {
        // For each existing trails
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (ElytraTrails trail : ElytraTrails.values())
        {
            if (trail != ElytraTrails.NONE)
            {
                boolean hasTrail = CosmeticManager.hasCosmetic(player.getUUID(), CosmeticType.ELYTRA_TRAIL,trail);
                String lore = NEW_LINE + ( hasTrail
                                          ? ChatColor.GREEN + "Débloqué"
                                          : ChatColor.RED + "Bloqué");
                addButton(trail.getIndex(), trail.getMaterial(), trail.getName(), lore,
                                    player.getActiveTrail() == trail, event -> {
                            if (hasTrail)
                                player.setActiveTrail(trail);
                            else
                                viewer.sendMessage(new ComponentBuilder(
                                        ChatColor.GOLD + "[" + ChatColor.YELLOW + "Accès à la boutique" + ChatColor.GOLD
                                                + "]")
                                                           .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                                                                 "https://tesseract.craftingstore.net/"))
                                                           .create());
                            this.close();
                        });
            }
            else
            {
                addButton(31, trail.getMaterial(), trail.getName(), null, player.getActiveTrail() == trail,
                                    event -> {
                                        player.setActiveTrail(trail);
                                        this.close();
                                    });
            }
        }

        addBackButton();
        addQuitButton();
        super.open(viewer);
    }
}
