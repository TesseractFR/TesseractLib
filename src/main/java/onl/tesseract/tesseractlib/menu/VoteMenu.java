package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.tesseractlib.util.Util;
import onl.tesseract.tesseractlib.util.menu.Button;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import onl.tesseract.tesseractlib.vote.VoteManager;
import onl.tesseract.tesseractlib.vote.VoteSite;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.Map;

public class VoteMenu extends InventoryMenu {
    private final TPlayer player;

    public VoteMenu(final TPlayer player)
    {
        super(27, ChatColor.GOLD + "Votes");
        this.player = player;
    }

    @Override
    public void open(final Player viewer)
    {
        fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        addQuitButton();
        addQuitButton(18);

        new BukkitRunnable() {
            @Override
            public void run()
            {
                Map<VoteSite, Duration> remainingDurations = VoteManager.getInstance().getRemainingTimeUntilVote(player);

                ItemLoreBuilder lore = new ItemLoreBuilder();
                remainingDurations.forEach((voteSite, duration) -> {
                    lore.newline()
                        .append(voteSite.serviceName(), NamedTextColor.YELLOW)
                        .append(" : ", NamedTextColor.GRAY);
                    if (duration.isZero() || duration.isNegative())
                        lore.append("Va voter !", NamedTextColor.GREEN);
                    else
                        lore.append(Util.getPrintableDuration(duration), NamedTextColor.RED);
                });
                lore.newline(2)
                    .append("Clic pour obtenir les liens", NamedTextColor.AQUA);

                addButton(9, new Button(new ItemBuilder(Material.COMMAND_BLOCK)
                        .name("Tous les sites", NamedTextColor.GOLD)
                        .lore(lore.get())
                        .build(), event -> {
                    close();
                    sendVoteLinks(viewer);
                }));
            }
        }.runTaskAsynchronously(TesseractLib.instance);

        super.open(viewer);
    }

    public static void sendVoteLinks(final Audience player)
    {
        VoteManager.getInstance().getVoteSites()
                   .forEach(voteSite -> sendVoteLink(player, voteSite));
    }

    public static void sendVoteLink(final Audience player, final VoteSite site)
    {
        player.sendMessage(Component.text(site.serviceName(), NamedTextColor.YELLOW)
                                    .append(Component.text(" : ", NamedTextColor.GRAY))
                                    .append(Component.text(site.address(), NamedTextColor.GOLD)
                                                     .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, site.address()))));
    }
}
