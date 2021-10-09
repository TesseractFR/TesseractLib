package onl.tesseract.tesseractlib.vote.goal;

import org.bukkit.entity.Player;

/**
 * A reward that can be given to a player. Custom rewards should implement this class, along with an implementation of {@link VoteGoalRewardType}
 */
public interface VoteGoalReward {
    /**
     * Get associated reward type
     */
    VoteGoalRewardType getType();

    void give(final Player player);

    void giveAll();

    /**
     * Serializes the reward
     */
    String serialize();
}
