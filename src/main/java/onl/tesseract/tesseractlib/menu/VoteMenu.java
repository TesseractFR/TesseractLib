package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.bddfacade.VoteRepository;
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
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Map;

public class VoteMenu extends InventoryMenu {
    private final TPlayer player;
    private BukkitTask buttonsTask;

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

        this.buttonsTask = new BukkitRunnable() {
            @Override
            public void run()
            {
                if (!hasViewers())
                {
                    close();
                    return;
                }
                Map<VoteSite, Duration> remainingDurations = VoteManager.getInstance().getRemainingTimeUntilVote(player);

                putAllSitesButton(remainingDurations, viewer);
            }
        }.runTaskTimerAsynchronously(TesseractLib.instance, 0, 20);

        new BukkitRunnable() {
            @Override
            public void run()
            {
                Map<VoteSite, Duration> remainingDurations = VoteManager.getInstance().getRemainingTimeUntilVote(player);
                int index = 10;
                for (VoteSite site : VoteManager.getInstance().getVoteSites())
                {
                    putSiteButton(site, remainingDurations.get(site), viewer, index++);
                }
            }
        }.runTaskAsynchronously(TesseractLib.instance);

        super.open(viewer);
    }

    @Override
    public void close()
    {
        buttonsTask.cancel();
        Runnable superMethod = super::close;
        new BukkitRunnable() {
            @Override
            public void run()
            {
                superMethod.run();
            }
        }.runTask(TesseractLib.instance);
    }

    private void putSiteButton(final VoteSite voteSite, final Duration remainingDuration, final Audience viewer, final int index)
    {
        ItemLoreBuilder lore = new ItemLoreBuilder().newline();
        if (remainingDuration.isZero() || remainingDuration.isNegative())
            lore.append("Va voter !", NamedTextColor.GREEN);
        else
            lore.append(Util.getPrintableDuration(remainingDuration), NamedTextColor.RED);

        lore.newline()
            .append("Mes votes ce mois-ci : ", NamedTextColor.GRAY)
            .append("" + VoteRepository.getMonthlyVote(player.getUUID(), voteSite.serviceName()))
            .newline()
            .append("Mes votes (total) : ", NamedTextColor.GRAY)
            .append("" + VoteRepository.getAllVotes(player.getUUID(), voteSite.serviceName()))
            .newline()
            .horizontalLine(40, NamedTextColor.YELLOW)
            .newline()
            .append("Tous les votes ce mois-ci : ", NamedTextColor.GRAY)
            .append("" + VoteRepository.getMonthlyVote(voteSite.serviceName()))
            .newline()
            .append("Tous les votes (total) : ", NamedTextColor.GRAY)
            .append("" + VoteRepository.getAllVotes(voteSite.serviceName()))
            .newline(2)
            .append("Clique pour obtenir le lien", NamedTextColor.AQUA);

        boolean canVote = remainingDuration.isNegative() || remainingDuration.isZero();
        addButton(index, new Button(new ItemBuilder(canVote ? Material.EMERALD_BLOCK : Material.STRUCTURE_VOID)
                .name(voteSite.serviceName(), NamedTextColor.YELLOW)
                .lore(lore.get())
                .build(), event -> {
            close();
            sendVoteLink(viewer, voteSite);
        }));
    }

    private void putAllSitesButton(final Map<VoteSite, Duration> remainingDurations, final Audience viewer)
    {
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
