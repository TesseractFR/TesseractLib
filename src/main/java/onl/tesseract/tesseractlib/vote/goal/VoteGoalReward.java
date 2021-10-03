package onl.tesseract.tesseractlib.vote.goal;

import org.bukkit.entity.Player;

public interface VoteGoalReward {
    VoteGoalRewardType getType();

    void give(final Player player);

    void giveAll();
}
