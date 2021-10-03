package onl.tesseract.tesseractlib.vote.goal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import onl.tesseract.tesseractlib.util.ChatFormats;
import org.bukkit.Bukkit;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;

public class VoteGoalManager {
    private final Collection<VoteGoal> goals = new HashSet<>();

    public void update()
    {
        Collection<VoteGoal> currentGoals = VoteGoalRepository.getCurrentGoals();

        // Determine newly started goals
        for (VoteGoal currentGoal : currentGoals)
        {
            if (!goals.contains(currentGoal))
            {
                goals.add(currentGoal);
                onNewGoal(currentGoal);
            }
        }

        for (VoteGoal goal : goals)
        {
            if (!currentGoals.contains(goal))
            {
                currentGoals.remove(goal);
                onGoalFinished(goal);
            }
        }
    }

    private void onNewGoal(final VoteGoal goal)
    {
        String duration = goal.getPrintableDuration();
        Component[] components = new Component[] {
                Component.text("                                                                       ", NamedTextColor.RED, TextDecoration.STRIKETHROUGH),
                Component.text("    ")
                         .append(Component.text("lll", NamedTextColor.WHITE, TextDecoration.OBFUSCATED))
                         .append(Component.text(" VOTE GOAL  " + duration, NamedTextColor.GOLD))
                        .append(Component.text("lll", NamedTextColor.WHITE, TextDecoration.OBFUSCATED)),
                Component.text("                     → ", NamedTextColor.RED, TextDecoration.BOLD)
                         .append(Component.text("/vote", NamedTextColor.YELLOW, TextDecoration.BOLD))
                         .append(Component.text(" ← ", NamedTextColor.RED, TextDecoration.BOLD))
                        .clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/vote")),
                Component.text("                                                                       ", NamedTextColor.RED, TextDecoration.STRIKETHROUGH),
                };

        Bukkit.getOnlinePlayers().forEach(p -> {
            for (var component : components)
                p.sendMessage(component);
        });
    }

    private void onGoalFinished(final VoteGoal goal)
    {
        int voteCount = VoteGoalRepository.getVoteCount(new Date(goal.getStart().toEpochMilli()), new Date(goal.getEnd().toEpochMilli()));
        if (voteCount < goal.getRequiredQuantity())
        {
            Component component = ChatFormats.VOTE.append(Component.text("Le vote goal n'a pas été atteint ='("));
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(component));
            return;
        }

        final VoteGoalReward reward = goal.getReward();
        reward.giveAll();
        Bukkit.getOnlinePlayers().forEach(reward::give);
    }
}
