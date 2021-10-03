package onl.tesseract.tesseractlib.vote.goal;

/**
 * Defines a type of reward.
 */
public interface VoteGoalRewardType {
    /**
     * Named used to identify the type
     */
    String getName();

    /**
     * Deserialize a reward from a raw string
     */
    VoteGoalReward deserialize(final String raw);
}

