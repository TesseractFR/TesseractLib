package onl.tesseract.tesseractlib.vote.goal;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class VoteGoalRewardManager {
    private static final HashMap<Integer, VoteGoalRewardType> goalToReward = new HashMap<>();
    private static final HashMap<String, VoteGoalRewardType> rewardTypes = new HashMap<>();

    public static void registerRewardType(final VoteGoalRewardType reward)
    {
        rewardTypes.put(reward.getName(), reward);
    }

    @Nullable
    public static VoteGoalRewardType getRewardType(final String name)
    {
        return rewardTypes.get(name);
    }
}
