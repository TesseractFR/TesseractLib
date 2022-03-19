package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import onl.tesseract.tesseractlib.vote.goal.VoteGoal;
import onl.tesseract.tesseractlib.vote.goal.VoteGoalManager;
import onl.tesseract.tesseractlib.vote.goal.VoteGoalRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class VoteMenu extends InventoryMenu {
    private static Class<? extends AVoteRewardMenu> rewardMenuClass = DefaultVoteRewardMenu.class;

    private final TPlayer player;
    private BukkitTask buttonsTask;

    public VoteMenu(final TPlayer player)
    {
        super(27, ChatColor.RED + "Votes");
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
                    buttonsTask.cancel();
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
                for (; index < 18; index++)
                {
                    add(index, Material.BARRIER, Component.empty());
                }

                putVoteGoalButton();
                putPlayerButton();
                putTopButton();
                putRewardButton();
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

    private void putRewardButton()
    {
        int keys = VoteRepository.getKeys(player.getUUID());
        addButton(22, new Button(new ItemBuilder(Material.RAW_GOLD)
                .name("Récompenses", NamedTextColor.GOLD)
                .lore(new ItemLoreBuilder().newline()
                                           .append("Mes points de vote", NamedTextColor.YELLOW)
                                           .append(" : ", NamedTextColor.GRAY)
                                           .append("" + keys, NamedTextColor.GOLD)
                                           .newline(2)
                                           .append("Cliquez pour voir les différentes récompenses", NamedTextColor.AQUA, TextDecoration.ITALIC)
                                           .get())
                .build(), event -> {
            try
            {
                Constructor<? extends AVoteRewardMenu> constructor = rewardMenuClass.getDeclaredConstructor(TPlayer.class, InventoryMenu.class);
                InventoryMenu menu = constructor.newInstance(player, this);
                menu.open(player.getBukkitPlayer());
            }
            catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
            {
                TesseractLib.logger().log(Level.SEVERE, "Failed to instantiate reward menu", e);
            }
        }));
    }

    private void putVoteGoalButton()
    {
        ItemLoreBuilder lore = new ItemLoreBuilder(45).newline()
                                                      .append("Vote pendant les ", NamedTextColor.YELLOW)
                                                      .append("Vote Goals", NamedTextColor.GOLD)
                                                      .append(" pour obtenir encore plus de récompenses !", NamedTextColor.YELLOW)
                                                      .newline()
                                                      .append("Si tu as voté pendant un vote goal, tu recevras à la fin de celui-ci une récompense différente à chaque goal.", NamedTextColor.GRAY)
                                                      .newline(2);

        Collection<VoteGoal> goals = VoteGoalManager.getGoals();
        if (goals.isEmpty())
            lore.append("Il n'y a pas de Vote Goal en cours pour le moment...", NamedTextColor.GRAY);
        else
        {
            for (VoteGoal goal : goals)
            {
                int voteCount = VoteGoalRepository.getVoteCount(goal);
                lore.append("→ ", NamedTextColor.RED)
                    .append("" + voteCount, NamedTextColor.YELLOW)
                    .append("/", NamedTextColor.GRAY)
                    .append("" + goal.requiredQuantity(), NamedTextColor.YELLOW)
                    .append(" | ", NamedTextColor.WHITE, TextDecoration.OBFUSCATED)
                    .append("Temps restant : ", NamedTextColor.GRAY)
                    .append(goal.getPrintableRemainingDuration(), NamedTextColor.YELLOW)
                    .append(" | ", NamedTextColor.WHITE, TextDecoration.OBFUSCATED)
                    .append(" Récompense : ", NamedTextColor.GRAY)
                    .append(goal.reward().toString(), NamedTextColor.YELLOW)
                    .newline();
            }
        }

        addButton(2, new Button(new ItemBuilder(Material.CLOCK)
                .name("Vote Goals", NamedTextColor.GOLD)
                .lore(lore.get())
                .build()));
    }

    private void putTopButton()
    {
        ItemLoreBuilder lore = new ItemLoreBuilder();
        LinkedHashMap<UUID, Integer> top = VoteRepository.getTop();
        int index = 1;
        for (Map.Entry<UUID, Integer> entry : top.entrySet())
        {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            lore.newline()
                .append(index++ + ". ", NamedTextColor.RED)
                .append(offlinePlayer.getName(), NamedTextColor.YELLOW)
                .append(" : ", NamedTextColor.GRAY)
                .append("" + entry.getValue(), NamedTextColor.GOLD);
        }

        lore.newline(2)
            .append("Récompenses pour les top voteurs :", NamedTextColor.YELLOW, TextDecoration.BOLD)
            .newline().append("1er : ", NamedTextColor.YELLOW).append("250 Lys d'or", NamedTextColor.GOLD)
            .newline().append("2e : ", NamedTextColor.YELLOW).append("150 Lys d'or", NamedTextColor.GOLD)
            .newline().append("3e : ", NamedTextColor.YELLOW).append("50 Lys d'or", NamedTextColor.GOLD);

        addButton(6, new Button(new ItemBuilder(Material.DIAMOND)
                .name("Top Voteurs", NamedTextColor.GOLD)
                .lore(lore.get())
                .build()));
    }

    private void putPlayerButton()
    {
        ItemLoreBuilder lore = new ItemLoreBuilder()
                .newline()
                .append("Aujourd'hui : ", NamedTextColor.YELLOW)
                .append("" + new VoteRepository.GetVoteStatementBuilder().setPeriod(VoteRepository.VotePeriod.DAILY)
                                                                         .setPlayerUUID(player.getUUID())
                                                                         .build(), NamedTextColor.GOLD)
                .newline()
                .append("Semaine : ", NamedTextColor.YELLOW)
                .append("" + new VoteRepository.GetVoteStatementBuilder().setPeriod(VoteRepository.VotePeriod.WEEKLY)
                                                                         .setPlayerUUID(player.getUUID())
                                                                         .build(), NamedTextColor.GOLD)
                .newline()
                .append("Mois : ", NamedTextColor.YELLOW)
                .append("" + new VoteRepository.GetVoteStatementBuilder().setPeriod(VoteRepository.VotePeriod.MONTHLY)
                                                                         .setPlayerUUID(player.getUUID())
                                                                         .build(), NamedTextColor.GOLD)
                .newline()
                .append("Total : ", NamedTextColor.YELLOW)
                .append("" + new VoteRepository.GetVoteStatementBuilder().setPlayerUUID(player.getUUID())
                                                                         .build(), NamedTextColor.GOLD);
        addButton(4, new Button(new ItemBuilder(getHead(player.getUUID()))
                .name(player.getOfflinePlayer().getName(), NamedTextColor.GOLD)
                .lore(lore.get())
                .build()));
    }

    private void putSiteButton(final VoteSite voteSite, final Duration remainingDuration, final Audience viewer, final int index)
    {
        ItemLoreBuilder lore = new ItemLoreBuilder().newline();
        if (remainingDuration.isZero() || remainingDuration.isNegative())
            lore.append("Va voter !", NamedTextColor.GREEN);
        else
            lore.append("Temps restant : ", NamedTextColor.GRAY)
                .append(Util.getPrintableDuration(remainingDuration), NamedTextColor.RED);

        lore.newline(2)
            .append("Mes votes ce mois-ci : ", NamedTextColor.GRAY)
            .append("" + new VoteRepository.GetVoteStatementBuilder().setPeriod(VoteRepository.VotePeriod.MONTHLY)
                                                                     .setPlayerUUID(player.getUUID())
                                                                     .setService(voteSite.serviceName())
                                                                     .build(), NamedTextColor.GOLD)
            .newline()
            .append("Mes votes (total) : ", NamedTextColor.GRAY)
            .append("" + new VoteRepository.GetVoteStatementBuilder().setPlayerUUID(player.getUUID())
                                                                     .setService(voteSite.serviceName())
                                                                     .build(), NamedTextColor.GOLD)
            .newline()
            .horizontalLine(40, NamedTextColor.YELLOW)
            .newline()
            .append("Tous les votes ce mois-ci : ", NamedTextColor.GRAY)
            .append("" + new VoteRepository.GetVoteStatementBuilder().setPeriod(VoteRepository.VotePeriod.MONTHLY)
                                                                     .setService(voteSite.serviceName())
                                                                     .build(), NamedTextColor.GOLD)
            .newline()
            .append("Tous les votes (total) : ", NamedTextColor.GRAY)
            .append("" + new VoteRepository.GetVoteStatementBuilder().setService(voteSite.serviceName())
                                                                     .build(), NamedTextColor.GOLD)
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
            .append("Clique pour obtenir les liens", NamedTextColor.AQUA);

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

    public static void setRewardMenuClass(final Class<? extends AVoteRewardMenu> rewardMenuClass)
    {
        VoteMenu.rewardMenuClass = rewardMenuClass;
    }
}
