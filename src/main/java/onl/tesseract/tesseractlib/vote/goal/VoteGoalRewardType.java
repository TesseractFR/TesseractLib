package onl.tesseract.tesseractlib.vote.goal;

public interface VoteGoalRewardType {
    String getName();

    String toString();

    VoteGoalReward deserialize(final String raw);
}

